#### SASJ
SASJ is a sample student administration system built with Spring Boot. It helps with managing and tracking the lessons taken by the students in an imaginary school. The system does not conform to any specific law regulation and requirements.

Demo: [https://ancient-garden-93311.herokuapp.com/](https://ancient-garden-93311.herokuapp.com/)

The application (SAS) manages only one school at the moment. It comes with localized versions in Bulgarian and English. It supports the following roles:
* Student. A student can see the weekly schedule with lessons; marks, absences, and notes for each subject.
* Teacher. A teacher can view the weekly schedule with lessons s/he has to teach. He or she is also able to see his/her students and how they perform at school.
* Principal. A principal administers all the school processes. The principal can change the weekly schedule, manage the classes with students and the courses led by teachers.

##### Application structure
The application structure follows the standard one for a Spring Boot application. The model is anemic one described by entities (annotated with @Entity). CRUD operations are performed through repositories (interfaces extending CrudRepository<>). Controllers contain the business logic by working with entities, repositories and some utility services. Thymeleaf views render results to the clients. Data is stored in H2 in-memory SQL engine.

The main model classes are:
* School. It is not persisted yet and serves mainly as a service. It keeps the current school year and its terms.
* Student: studies in classes, has marks, notes, and absences. It is a user with extra staff.
* Teacher: leads courses, is a form-master. The teacher is also a user with some extra staff.
* School class: has students, a grade, a weekly schedule with lessons (shared by all students in the school class), has a room.
* User. The entity keeps standard data about users like username, email, password, and additional personal information like name, age, and address.

For demonstration purposes the application generates students, teachers, and school classes on every start. All data is kept in memory and is lost after restart.

Beside the standard Spring boot code there is some additional infrastructure that helps with the development of controllers and views:
* SecuredController: every controller can tell what kind of users can access its views. This helps with the decentralization of the authorization configuration.
* LocalizedController: an interface that marks the controller as a one that has multilingual support. This is used when generating URLs for the views of the controller.
* UrlLocaleResolver: a custom resolver that looks at the beginning of the URL for a locale.
* UrlBuilder: helps with building of URLs in a more type safe manner.
