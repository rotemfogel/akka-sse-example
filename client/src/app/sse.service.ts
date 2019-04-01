import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from './user';

@Injectable({
  providedIn: 'root'
})
export class SseService {

  readonly url = 'http://localhost:8000/users';
  users: User[] = [];

  constructor(private http: HttpClient) {
  }

  public stream(): Observable<Array<User>> {
    return Observable.create((observer) => {
      const eventSource = new EventSource(this.url);
      eventSource.onmessage = (evt) => {
        console.log('Received event: ', evt);
        if (evt.data !== '') {
          const json = JSON.parse(evt.data);
          this.users.push(new User(json.id, json.name));
        }
        observer.next(this.users);
      };
      eventSource.onerror = (error) => {
        // readyState === 0 (closed) means the remote source closed the connection,
        // so we can safely treat it as a normal situation. Another way of detecting the end of the stream
        // is to insert a special element in the stream of violations, which the client can identify as the last one.
        if (eventSource.readyState === 0) {
          console.log('The stream has been closed by the server.');
          eventSource.close();
          observer.complete();
        } else {
          observer.error('EventSource error: ' + error);
        }
      };
    });
  }
}
