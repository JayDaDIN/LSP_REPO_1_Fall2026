# Assignment #3 — Design Discussion

**Student:** Justin Dunbar
**Course:** CSCI 363 Large Scale Programming, Fall 2026

Assignment #3 is an object-oriented refactoring of my Assignment #2 employee payroll ETL
pipeline. The behavior is unchanged — same input file, same output file, same console
summary. Only the design changed.

---

## 1. How was my Assignment #2 solution organized?

Assignment #2 was a **single class**, `ETLPipeline`, containing 252 lines: a private
constructor, nine `static` methods, and nine `static final` constants. It worked, but it
was procedural code written in Java syntax rather than an object-oriented design:

- **No objects existed.** Data moved through the program as `String[]` from `split()`,
  as loose local variables inside `transformRow`, and as a `String` produced by
  `String.join`. There was never a thing called an employee — only fields that happened
  to describe one.
- **One method held most of the logic.** `transformRow` did five unrelated jobs:
  checked the field count, trimmed and uppercased, parsed and validated numbers,
  triggered the pay calculation, and formatted the output row.
- **Unrelated concerns shared one file.** File paths, the CSV header, payroll rules,
  pay-band thresholds, and console formatting all lived side by side, so a change to any
  one of them meant editing the same class.
- **Weak signalling.** `transformRow` returned `null` to mean "skip this row," which the
  caller had to remember to test for. Pay levels were returned as bare strings, so
  `"Excecutive"` would have compiled and shipped.
- **Hard-wired to one location.** Input and output paths were `static final` constants,
  so nothing could be reused or tested against a different file.

## 2. What design changes did I make for Assignment #3?

I decomposed the single class into **seven classes and two enums**, each owning one
responsibility, and introduced a real domain object so data travels as an `Employee`
rather than as strings and arrays.

`ETLPipeline` no longer performs the work. It constructs the three stages, runs them in
order, and reports the result — its `run()` method is about twenty lines.

## 3. What classes or abstractions did I introduce, and why?

| Class | Responsibility | Why it exists |
|---|---|---|
| `ETLPipeline` | Wires the stages together, runs extract → transform → load | Orchestration is its own job, separate from doing the work |
| `EmployeeCsvReader` | Reads raw lines, drops the header | Isolates input file access; knows nothing about payroll |
| `EmployeeRowParser` | One raw line → `Optional<Employee>` | Owns every rule about what a valid input row looks like |
| `PayrollCalculator` | Overtime, IT bonus, round-half-up | Business rules in one place; changing the bonus touches one file |
| `Employee` | Immutable domain object; renders its own CSV row | Gives the data an identity and puts behavior next to the data it uses |
| `EmployeeCsvWriter` | Writes header and rows | Isolates output file access and the file format |
| `RunSummary` | Holds the counts, prints the summary | Gives the skipped-row arithmetic and console output a home |
| `PayLevel` (enum) | `forGrossPay()` classification | Makes the four bands a compiler-checked closed set |
| `EmploymentStatus` (enum) | `forHours()` classification | Same reasoning as `PayLevel` |

Three specific improvements are worth calling out:

**`Employee` is immutable and computes its derived state once.** Its constructor takes
the five validated input fields plus a `PayrollCalculator`, then computes gross pay, pay
level, and employment status. An `Employee` therefore cannot exist in a half-populated
state. I deliberately kept the pay *rules* in `PayrollCalculator` rather than inside
`Employee`, so the rules can change without modifying the domain object.

**Enums replaced stringly-typed values.** In Assignment #2, `determinePayLevel` returned
`"Low"` or `"Executive"` as plain strings from an `if` chain. Now `PayLevel` is a type
with four constants and the threshold logic attached, so an invalid pay level is not
expressible.

**`Optional<Employee>` replaced `return null`.** The possibility of "no employee here"
is now stated in the return type instead of being a convention the caller must know.

## 4. How did I divide responsibilities differently?

Assignment #2 divided work by **step in the process** — one method per stage, all sharing
the same class's constants and state. Assignment #3 divides by **reason to change**:

- Changing the input file format affects only `EmployeeCsvReader` and `EmployeeRowParser`.
- Changing the overtime multiplier or bonus affects only `PayrollCalculator`.
- Changing the pay bands affects only `PayLevel`.
- Changing the output columns affects only `Employee.toCsvRow()` and `EmployeeCsvWriter`.
- Changing the console wording affects only `RunSummary`.

None of those changes requires touching `ETLPipeline`. In Assignment #2, every one of
them meant editing the same 252-line file.

Paths also moved from `static final` constants into constructor arguments, so the reader
and writer are no longer welded to one location on disk.

## 5. Why do I believe this design is an improvement?

- **Single responsibility.** Each class has one reason to change, and each is small
  enough to read in one sitting. The largest, `Employee`, is 151 lines including Javadoc;
  most are under 100. Assignment #2 was one 252-line file.
- **Encapsulation.** `Employee` owns its data and exposes behavior (`toCsvRow()`) rather
  than letting callers assemble a row from loose fields.
- **The type system does more work.** Enums and `Optional` move two whole categories of
  mistake — misspelled classifications and forgotten null checks — from runtime to
  compile time.
- **Testable in pieces.** `EmployeeRowParser` can be exercised on a plain string and
  `PayrollCalculator` on two numbers, with no file access.
- **Honest about what it is not.** I considered adding `Extractor` / `Transformer` /
  `Loader` interfaces. I left them out: each would have exactly one implementation, and
  the assignment warns against classes that exist only to raise the count. If a second
  input format were ever required, extracting an interface then would be a small change.

**Verification.** The refactoring was checked against Assignment #2 rather than trusted.
Both versions were run on the grading dataset and on a twelve-row edge-case file
(half-up rounding at `1 × 2.675 → 2.68`, pay-band boundaries at exactly `500.00`,
`1000.00`, and `2000.00`, part/full-time at `29.99` and `30.00`, exactly 40 hours,
`" IT "` versus `"it"`, and a negative rate). The two programs produced **byte-identical
output files** in both cases, and identical console summaries.

---

## AI and Internet Resources

**AI tools used:** Yes. I used Claude (Anthropic) via Claude Code to assist with this
assignment.

**Transcript:** <!-- TODO: replace this line with the transcript link before submitting -->
`TODO — paste link to ai-transcript.md here`

Once `ai-transcript.md` is committed alongside this file, the link is:
`https://github.com/JayDaDIN/LSP_REPO_1_Fall2026/blob/main/src/org/howard/edu/lsp/assignment3/doc/ai-transcript.md`

**Internet resources used:** None. All information used came from the assignment
specification, the course syllabus, and the AI interaction linked above.

I remain responsible for understanding, testing, and being able to explain the code and
design submitted here.
