create table USERS(
  id BIGINT not null AUTO_INCREMENT,
  login TEXT not null,
  password TEXT not null,
  PRIMARY KEY ( id ),
  UNIQUE (login)
);