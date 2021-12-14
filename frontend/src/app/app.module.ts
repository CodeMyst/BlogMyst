import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";

import { AppRoutingModule } from "./app-routing.module";
import { AppComponent } from "./app.component";
import { NavigationComponent } from './navigation/navigation.component';
import { LoginComponent } from './login/login.component';
import { FeedComponent } from './feed/feed.component';
import { RegisterComponent } from './register/register.component';

@NgModule({
    declarations: [
        AppComponent,
        NavigationComponent,
        LoginComponent,
        FeedComponent,
        RegisterComponent
    ],
    imports: [
        BrowserModule,
        AppRoutingModule
    ],
    providers: [],
    bootstrap: [AppComponent]
})
export class AppModule { }
