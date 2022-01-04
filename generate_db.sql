create database if not exists blogmyst;

create table user
(
    id            int                           not null auto_increment primary key,
    username      varchar(20)                   not null,
    role          enum ('ADMIN', 'MOD', 'USER') not null,
    password_hash tinytext                      not null,
    salt          tinytext                      not null
);

create table blog
(
    id          int      not null auto_increment primary key,
    author_id   int      not null,
    name        tinytext not null,
    description text     not null,

    constraint fk_blog_author
        foreign key (author_id)
            references user (id)
);

create table post
(
    id         int        not null auto_increment primary key,
    blog_id    int        not null,
    created_at datetime   not null,
    last_edit  datetime,
    title      tinytext   not null,
    content    mediumtext not null,
    upvotes    int        not null,
    downvotes  int        not null,

    constraint fk_post_blog
        foreign key (blog_id)
            references blog (id)
);

create table comment
(
    id         int      not null auto_increment primary key,
    author_id  int      not null,
    post_id    int      not null,
    created_at datetime not null,
    last_edit  datetime,
    content    text,

    constraint fk_comment_author
        foreign key (author_id)
            references user (id),

    constraint fk_comment_post
        foreign key (post_id)
            references post (id)
);

create table report
(
    id         int      not null auto_increment primary key,
    user_id    int      not null,
    type       enum ('BLOG', 'POST', 'COMMENT'),
    date       datetime not null,
    reason     text,

    subject_id int      not null,

    constraint fk_report_user
        foreign key (user_id)
            references user (id)
);

create table blog_follow
(
    user_id int not null,
    blog_id int not null,

    primary key (user_id, blog_id),

    constraint fk_blog_follow_user
        foreign key (user_id)
            references user (id),

    constraint fk_blog_follow_blog
        foreign key (blog_id)
            references blog (id)
);