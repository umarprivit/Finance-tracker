# Mobile App Development
**Team Members:**
* Syed Umer (023-22-0196)
* Prinka Chugh (023-22-0182)

---

# Project Report: Finance Tracker Application

## 1. Introduction & Problem Statement
In today's fast-paced world, managing personal finances efficiently is a significant challenge for many individuals. People often struggle to keep track of their daily expenses, balance their budgets, and stay committed to their financial goals. Traditional methods of record-keeping are tedious and prone to errors. 

The **Finance Tracker** application is designed to solve this problem by providing a seamless, on-the-go solution for tracking income and expenses. By offering intuitive dashboards, categorized budget management, and visual goal tracking, the app empowers users to take complete control of their financial health directly from their smartphones, without the need for complex spreadsheets.

## 2. Project Objectives
* **Simplify Expense Tracking:** Allow users to quickly input transactions (income and expenses) with categorization.
* **Budget Management:** Help users set and monitor monthly budgets for different categories to prevent overspending.
* **Financial Goal setting:** Enable users to set saving goals (e.g., buying a car, emergency fund) and track their progress visually.
* **Insightful Analytics:** Provide easily readable charts and visual representations of the user's cash flow.
* **Secure Local Storage:** Keep user data completely private and offline on the device.

## 3. Core Features
* **Dashboard Overview:** A centralized hub displaying the current balance, recent transactions, and summarized cash flow charts.
* **Transaction Management:** Full CRUD (Create, Read, Update, Delete) capabilities for daily incomes and expenses.
* **Budget Tracking:** Categorized budgeting with real-time progress indicators showing how much of the budget has been utilized.
* **Savings Goals (Goal Jars):** Interactive and visual "Goal Jars" that fill up as users add funds toward specific financial targets.
* **Settings & Customization:** Application settings to configure user preferences.

## 4. Technology Stack
The application is built using the latest modern Android development standards recommended by Google:

* **Programming Language:** Kotlin (v1.9.22)
* **UI Framework:** Jetpack Compose (Material 3 Design System) for a declarative, reactive, and smooth user interface.
* **Architecture:** Clean Architecture combined with the MVVM (Model-View-ViewModel) design pattern.
* **Dependency Injection:** Dagger Hilt for scalable and decoupled component management.
* **Local Database:** Room persistence library for structured local SQLite data storage.
* **Asynchronous Programming:** Kotlin Coroutines and Flows for handling background tasks and reactive UI updates.
* **Navigation:** Jetpack Navigation Compose for seamless routing between screens.
* **Data Visualization:** Vico Compose for rendering beautiful and interactive charts.
* **Animations:** Lottie Compose for engaging motion graphics and UI feedback.
* **Build System:** Gradle (Kotlin DSL).

## 5. System Architecture
The application strictly adheres to **Clean Architecture** principles, separating concerns into three distinct layers to ensure scalability, testability, and maintainability:

1. **Presentation Layer (UI):** 
   * Handled inside `com.fintrack.app.ui`.
   * Utilizes Jetpack Compose for rendering UI elements.
   * Scoped by features: Dashboard, Goals, Transactions, Budgets, and Settings.
   * ViewModels manage the `UiState` and handle UI events, reacting to data streams from the Domain layer using Kotlin Flows.
2. **Domain Layer (Business Logic):**
   * Handled inside `com.fintrack.app.domain`.
   * Contains core business rules, Models (`Transaction`, `Budget`, `Goal`, `Category`), and Use Cases.
   * Defines Repository Interfaces to abstract the source of data.
   * Completely independent of any Android framework specific dependencies.
3. **Data Layer:**
   * Handled inside `com.fintrack.app.data`.
   * Implements the Repository interfaces defined in the Domain layer.
   * Contains the Local Data Source powered by Room Database (`daos` and `entities`).
   * Maps Database Entities (`TransactionEntity`, `GoalEntity`, etc.) to Domain Models before passing them up the stream.

## 6. Database Schema & Data Models
The local Room Database handles four primary entities:
1. **Transactions:** Stores individual records of money going in or out (Amount, Date, Type, Category ID).
2. **Categories:** Defines the categories for transactions (e.g., Groceries, Rent, Salary).
3. **Budgets:** Tracks spending limits associated with specific categories over a given time period.
4. **Goals:** Stores user savings targets, holding the target amount, the currently saved amount, and the goal name.

## 7. UI / UX Highlights
The application leverages modern UI/UX paradigms:
* **Material 3 Design:** Utilizes Dynamic Colors, modern typography, and standard M3 components (Bottom Sheets, Dialogs, Cards).
* **Custom Canvas Drawing:** Features a customized "Goal Jar Canvas" component that visually fills up as users save money, gamifying the savings experience.
* **Reactive UI:** The UI dynamically updates via state hoisting and `StateFlow`, ensuring the screen instantly reflects any added transaction or edited budget without needing a screen refresh.
* **Data Visualization:** Employs the Vico charting library to render intuitive graphs of complex financial data on the Dashboard.

## 8. Conclusion
The **Finance Tracker** app is a robust, well-architected Android application. By leveraging Clean Architecture, MVVM, and Jetpack Compose, the project proves to be highly scalable, maintainable, and aligned with modern industry standards. The inclusion of engaging elements like charts and visual goal tracking goes beyond basic data entry, providing users with a comprehensive tool to genuinely understand and improve their personal finances.