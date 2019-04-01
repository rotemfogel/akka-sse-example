import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from './user';
import { SseService } from './sse.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'akka-sse web client example';

  users: Observable<User[]>;

  constructor(private sse: SseService) {
  }

  stream(): void {
    this.users = this.sse.stream();
  }
}
