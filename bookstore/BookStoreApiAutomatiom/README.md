#  BookStore API Automation Project

This project automates REST API testing for a BookStore application using Java, RestAssured, TestNG, Cucumber, and Allure Reports for reporting.

---

##  Tech Stack

| Component | Version | Purpose                   |
| --------- | ------- | ------------------------- |
| Java      | 20      | Core programming language |
| Maven | Latest (compatible with Java 21) | Project build lifecycle & dependency management |
| TestNG   | Latest  | Test execution and suite configuration |
| Cucumber | 7.x     | BDD (Behavior-Driven Development)      |
| RestAssured | 5.4.0   | Simplified API testing using Java |
| Cucumber Reports | 6.7.0  | Generate beautiful test reports |

##   Why **Cucumber** reports

-  API Testing (RestAssured) → Shows cURL requests & responses.

-  ucumber uses reporter plugins to produce reports that contain information about
-  what scenarios have passed or failed. Some plugins are built-in, others have to be installed separately.


##  Why **TestNG** is a better fit because:

- Allure integrates more robustly.

- TestNG + Cucumber gives better control over test grouping, parallelism, and execution flow.

- You'll benefit from TestNG’s native support for dependencies, retries, and configuration.



##  Prerequisites

- Java 20 installed and added to PATH
- Maven installed and added to PATH
---

##  How to set up and  Run Tests

1) Create a new maven project for automation or clone from the github if already present
2) Fork the Dev repo given and make it up in the local machine , to run the automation (The steps will be present in README.md of Dev repo)
3) End Points automated covered in this automation are:

    * POST /signup – To sign up to the book store
    * POST /login – To login after sign up and generate a token
    * POST /books – Create a new book
    * PUT /books/{id} – Update an existing book
    * GET /books/{id} – Fetch a book by ID
    * GET /books – Fetch all books
    * DELETE /books/{id} – Delete a book

6) Execute the automation suite by running the **testng.xml** file. This will trigger all feature scenarios written in a human-readable format, ensuring comprehensive test coverage and clear visibility into the executed test cases.
7) Once done , Allure- reports will be generate and can be seen under Allure-reports/allure-results


# ** CI/CD Integration**

## **Prerequisites:**

Install Jenkins and necessary plugins - Git ,Github,Pipeline , Maven and Allure plugins ( these plugins can be installed via Jenkins UI )

Ngrok (for development purpose)

## **STEPS To be followed for CI/CD:** ( for development purpose - testing env)

1) Need to add jenkinsFile in Dev repo - which is to build dev code and trigger QA automation (for reference https://www.jenkins.io/doc/tutorials/build-a-java-app-with-maven/)

<pre lang="groovy"><code>pipeline { agent any stages { stage('Build Dev') { steps { echo 'Build or test dev code here' } } stage('Trigger QA Automation') { steps { build job: 'QA-Repo' } } } } </code></pre>


2) JenkinsFile in QA repo has to be included to run and generate report

<pre lang="groovy"><code>pipeline { agent any tools { maven 'Maven 3.6.3' allure 'Allure' } stages { stage('Checkout') { steps { git url: '&lt;gitUrl&gt;', branch: '&lt;BranchName&gt;' } } stage('Build and Test') { steps { sh 'mvn clean test' } } stage('Generate Allure Report') { steps { sh 'mvn allure:report' } } } post { always { allure([ includeProperties: false, jdk: '', results: [[path: 'target/allure-results']] ]) } } }</code></pre>

3) Launch the Jenkins ( using jenkins command ) in Local , once launched , install all the plugins needed
4) Create 2 jobs as type pipeline for configuring Dev and QA repo
5) Configure the repo in their respective jobs and also make the configuration as necessary
6) In Dev repo Webhooks - Need to add payload url for triggering the Dev Job whenever dev commits the code
7) Since github can't access your local , run ngrok command to let public access for your local server

           ngrok http http://localhost:8080 
8) It will generate the url , use that as payload url along with repo name (eg : https://gitUserName:gitPassword@ngrokServerProvided/job/DevRepo/build )//Replace with your dev repo name
9) Now commit any changes in Dev repo . The build will be triggered in Dev jenkins job and on success,QA automation job will be run and generate Allure report at last
10) Now for every commit dev makes , the Dev build and QA automation will get triggered .

