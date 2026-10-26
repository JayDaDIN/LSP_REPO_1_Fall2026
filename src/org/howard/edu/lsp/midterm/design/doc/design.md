# Question 2: CRC Cards

## Class: Student

**Responsibilities:** Store and expose a student's identifier, name, and contact details; enforce any basic validity rules for those values.  
**Collaborators:** `Registration`

## Class: Course

**Responsibilities:** Store a course code and its credit-hour value; expose course information needed for a registration.  
**Collaborators:** `Registration`

## Class: Registration

**Responsibilities:** Represent one student's registration in one course; retain the registration's student and course; expose its calculated tuition when available.  
**Collaborators:** `Student`, `Course`, `TuitionCalculator`

## Class: TuitionCalculator

**Responsibilities:** Calculate tuition for a registration and apply the heavy-course-load discount according to the pricing rules.  
**Collaborators:** `Registration`, `Course`

## Class: RegistrationService

**Responsibilities:** Coordinate a registration request; create or validate a `Registration`, request its tuition calculation, persist it, and arrange a confirmation. Report failures to its caller rather than hiding them.  
**Collaborators:** `Student`, `Course`, `Registration`, `TuitionCalculator`, `RegistrationRepository`, `ConfirmationSender`

## Class: RegistrationRepository (interface)

**Responsibilities:** Define the operation for saving a registration without specifying where it is stored.  
**Collaborators:** `Registration`, `FileRegistrationRepository`, `DatabaseRegistrationRepository`

## Class: ConfirmationSender (interface)

**Responsibilities:** Define the operation for sending a registration confirmation without specifying the delivery channel.  
**Collaborators:** `Registration`, `EmailConfirmationSender`, `TextConfirmationSender`

## Class: FileRegistrationRepository

**Responsibilities:** Append registration records to the local file using the `RegistrationRepository` contract.  
**Collaborators:** `RegistrationRepository`, `Registration`

## Class: DatabaseRegistrationRepository

**Responsibilities:** Save registration records in the configured database using the `RegistrationRepository` contract.  
**Collaborators:** `RegistrationRepository`, `Registration`

## Class: EmailConfirmationSender

**Responsibilities:** Send a registration confirmation through email using the `ConfirmationSender` contract.  
**Collaborators:** `ConfirmationSender`, `Registration`, `Student`

## Class: TextConfirmationSender

**Responsibilities:** Send a registration confirmation through text message using the `ConfirmationSender` contract.  
**Collaborators:** `ConfirmationSender`, `Registration`, `Student`
