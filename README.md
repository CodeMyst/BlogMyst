# Blog

## Description

Blogging platform.

Users can create their own blogs where they can post. Users can see blogs and posts from other users.

Users can comment on every post, comments and posts can be deleted by mods. Entire blogs can be deleted by admins (they can also delete users);

Users can follow other blogs and see only posts from followed blogs on the home page.

Users can upvote/downvote posts and reports blogs/posts/comments. Mods and admins can view those reports and delete content if needed.

## Roles

* Admin
* Moderator
* User

## Functionality

1. Admin
    - Delete/ban users
    - Edit users (roles)
    - Delete blogs

2. Moderator
    - Delete comments
    - Delete posts

3. User
    - View other profiles
    - View followed posts
    - View own profile
        - Edit profile (username, avatar, password)
        - Delete profile
    - View blogs
        - Follow/unfollow
        - View all posts
            - View a post
                - View comments
                - Add comments
                - Edit own comments
                - Delete own comments
                - Upvote/downvote posts
    - Create a blog
        - Create a post
            - Edit post
            - Delete post
    - Report post/blog/comments
    - Search post/blog/user

## Entities

```
enum user_role {
  user
  mod
  admin
}

enum report_type {
  blog
  post
  comment
}

table user {
  id int [pk, increment]
  username varchar
  role user_role
  password_hash varchar
  salt varchar
}

table blog {
  id int [pk, increment]
  author_id int [ref: > user.id]
  name varchar
  description varchar
}

table post {
  id int [pk, increment]
  blog_id int [ref: > blog.id]
  created_at datetime
  last_edit datetime
  title varchar
  content varchar
  upvotes int
  downvotes int
}

table comment {
  id int [pk, increment]
  author_id int [ref: > user.id]
  post_id int [ref: > post.id]
  created_at datetime
  last_edit datetime
  content varchar
}

table report {
  id int [pk, increment]
  user_id int [ref: > user.id]
  type report_type
  subject_id int // TODO
  date datetime
  reason varchar
}

table blog_follow {
  user_id int [ref: > user.id]
  blog_id int [ref: > blog.id]
}
```
