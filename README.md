# hw03-spring

В репозитории содержится проект ДЗ.

### Build instructions

```shell
$ git clone https://github.com/nekogda/aston-hw03.git
$ cd aston-hw03
$ ./mvnw package
```

Артефакт в формате `war`. Проверялся развертыванием на 10-м [tomcat](https://tomcat.apache.org/download-10.cgi).

### Description

Во время подготовки ДЗ захотелось себя наказать.
Поэтому пароль в User открытым текстом не хранится. 
Пользовательский пароль хешируется при получении и далее используется только хеш. 

#### Архитектура проекта
Onion со вкусом hexagonal (ports and adapters) и под музыку clean.

```text
user                          <-- root
├── app                       <-- application core
│   ├── domain                <-- domain model + _domain_ services
│   │   ├── model
│   │   └── service
│   ├── exception             <-- app exceptions
│   ├── port                  <-- i/o interfaces (ports)
│   │   ├── in
│   │   └── out
│   └── service               <-- app specific services (use-cases)
├── persistence               <-- repository impl (secondary adapter)
│   └── config
└── rest                      <-- rest controllers (primary adapter)
    ├── config
    ├── ctrl
    └── dto
```
Основной вызов классическому учению заключается в том, что термин `domain services`
в данном проекте означает именно доменный сервис, а не DAO/Repository и прочие паттерны.

Т.е. это сервисы, которые содержат кросс-агрегатную бизнес-логику. 

#### Api

Вместо openapi и swagger-ui

```text
#1 - GET  /app/users               REQ: none                   RESP: List<UserResponse>
#2 - GET  /app/users/{login}       REQ: none                   RESP: UserResponse
#3 - POST /app/users               REQ: UserCreateRequest      RESP: none
#4 - PUT  /app/users/{login}       REQ: ChangePasswordRequest  RESP: none
#5 - PUT  /app/login               REQ: LogInRequest           RESP: none
```
Примеры запросов для проверки

```shell
#1 - multiple get
curl -v -X GET http://localhost:8080/hw03-1.0-SNAPSHOT/app/users
#2 - single get
curl -v -X GET http://localhost:8080/hw03-1.0-SNAPSHOT/app/users/fooUser
#3 - create
curl -v -X POST http://localhost:8080/hw03-1.0-SNAPSHOT/app/users -H 'Content-Type: application/json' -d '{"login": "fooUserA", "password": "password"}'
#4 - change password
curl -v -X PUT http://localhost:8080/hw03-1.0-SNAPSHOT/app/users/fooUser -H 'Content-Type: application/json' -d '{"oldPassword": "password", "newPassword": "newPassword"}'
#5 - logIn
curl -v -X POST http://localhost:8080/hw03-1.0-SNAPSHOT/app/login -H 'Content-Type: application/json' -d '{"login": "fooUser", "password": "password"}'
```