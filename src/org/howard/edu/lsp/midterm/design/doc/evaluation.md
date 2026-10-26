# Question 2: Design Evaluation

`CourseRegistration` combines several distinct responsibilities: it stores student and course data, calculates tuition and applies a pricing rule, prints a confirmation, writes a file, simulates sending email, and records a timestamp. This conflicts with Riel's heuristic that a class should capture one key abstraction and gives the class many unrelated reasons to change. For example, a tuition policy change, a database migration, or a new notification channel would all require edits to this class.

The public fields also violate Riel's heuristic that data should be hidden within its class. Any caller can change `creditHours`, `studentId`, or other values without validation, potentially leaving a registration in an invalid or inconsistent state. Callers are coupled to the representation rather than a controlled public interface.

`registerStudent()` is doing too much and directly performs presentation, persistence, pricing, and notification work. Its direct use of `System.out`, `FileWriter`, and `Date` makes those concerns difficult to test independently or replace. It also catches every exception and only prints a stack trace, so the caller cannot tell whether registration actually succeeded; the writer is not protected by try-with-resources. The class therefore has poor separation of concerns and offers no clear seams for the evolving requirements.

An improved design should keep registration data encapsulated, put tuition calculation in a focused domain service, and delegate storage and notification through explicit collaborators. The registration workflow can coordinate these objects without implementing every detail itself.
