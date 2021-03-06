create database if not exists blogmyst;

create table user
(
    username      varchar(50)                             not null primary key,
    role          enum ('ADMIN', 'MOD', 'USER', 'BANNED') not null,
    password_hash tinytext                                not null,
    created_at    datetime                                not null,
    banned_at     datetime
);

create table blog
(
    url             varchar(50) not null primary key,
    author_username varchar(50) not null,
    name            tinytext    not null,
    description     text        not null,

    constraint fk_blog_author
        foreign key (author_username)
            references user (username)
);

create table post
(
    url        varchar(50) not null primary key,
    blog_url   varchar(50) not null,
    created_at datetime    not null,
    last_edit  datetime,
    title      tinytext    not null,
    content    mediumtext  not null,
    upvotes    int         not null,

    constraint fk_post_blog
        foreign key (blog_url)
            references blog (url)
);

create table comment
(
    id               int         not null auto_increment primary key,
    author_username  varchar(50) not null,
    post_url         varchar(50) not null,
    created_at       datetime    not null,
    last_edit        datetime,
    content          text,

    constraint fk_comment_author
        foreign key (author_username)
            references user (username),

    constraint fk_comment_post
        foreign key (post_url)
            references post (url)
);

create table report
(
    id            int         not null auto_increment primary key,
    user_username varchar(50) not null,
    type          enum ('BLOG', 'POST', 'COMMENT'),
    date          datetime    not null,
    reason        text,

    subject_id    varchar(200) not null,

    constraint fk_report_user
        foreign key (user_username)
            references user (username)
);

create table blog_follow
(
    user_username varchar(50) not null,
    blog_url      varchar(50) not null,

    primary key (user_username, blog_url),

    constraint fk_blog_follow_user
        foreign key (user_username)
            references user (username),

    constraint fk_blog_follow_blog
        foreign key (blog_url)
            references blog (url)
);
