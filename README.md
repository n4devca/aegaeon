# Aegaeon

Aegaeon [<sup>W</sup>](https://en.wikipedia.org/wiki/Aegaeon_(moon)) is an OpenID Connect / OAuth 2.0 server implementation using spring boot.

The code is available under Apache 2.0 license and is currently under heavy development.
The project was inspired by [mitreid-connect](https://github.com/mitreid-connect/OpenID-Connect-Java-Spring-Server).

The project was started with the goal of having a better understanding of OpenId / OAuth standard and has evolved to a project with the following goal :

- Creating an *opiniatre* implementation. i.e. Having sensible default and being pragmatic.
- Simple, fast and clean
- Being up-to-date

The goal of this project is not to support all aspects of OpenID and OAuth.
**Not recommend** features or features not making a lot of sense in 2017 will not be implemented.

## Features (completed and planned)

- OAuth: Implicit flow (ongoing)
- OAuth: Authorization Code (ongoing)
- OAuth: Client Credential
- OAuth: Full error handling (ongoing)
- OpenID: user info endpoint
- OpenID: introspect endpoint
- JWT using RSA signature (supported)
- JWT using HMAC Protection
- Clustering ready (ongoing)
- Basic Client administration
- API Scopes / Claims centric

## Notable Technologies

- Spring Framework 4.3.8
- Spring Boot 1.5.3
- Hibernate 5.0.12
- Thymeleaf 3.0.5
- Nimbus 4.23