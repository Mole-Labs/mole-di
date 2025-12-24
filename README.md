[한국어](https://github.com/Mole-Labs/mole-di/blob/main/README.ko.md)
[English](https://github.com/Mole-Labs/mole-di/blob/main/README.md)  

[![Release](https://jitpack.io/v/Mole-Labs/mole-di.svg)](https://jitpack.io/#Mole-Labs/mole-di)



# Mole-DI: A Simple, Fast, and Explicit DI Framework for Android

Mole-DI is a lightweight, runtime dependency injection framework for Kotlin,  
heavily inspired by Koin but designed with a focus on **explicitness**, predictability, and
performance.

It brings the power of **lexical scoping** to Android development with a clean and intuitive DSL.

# What makes Mole-DI different from Koin?

While Koin is an excellent and mature framework,   
Mole-DI aims to improve upon some of its "magic" by emphasizing a more explicit and transparent
dependency graph.

## 1. Statically Defined & Nested Scope Hierarchy

Mole-DI takes explicitness a step further by having you define the entire scope hierarchy
upfront, directly in your modules. You don't dynamically create scopes at the point of injection;
you define the static blueprint of your dependency graph and then resolve paths to the scopes you
need.
This is a key difference from Koin, where scope linking can sometimes feel implicit or scattered.

## 2. Lifecycle-Safe DSL Constraints

Mole-DI's DSL is not just about convenience; it's about correctness. The hierarchy is strictly
governed by Android's lifecycle rules:

### Mole-DI's Static & Nested Scope Definition:

```kotlin  
val appModule: ModuleDefinition = {
    // Root-level singletons (e.g., Application lifecycle)
    single { AppDatabase.create(get()) } // Assuming Context is declared

    // Define a nested scope
    scope<ParentScopeKey> {
        scope<ChildScopeKey> {
            scope<GrandChildScopeKey> {
                single { SomeRepository(get(), get<AppDatabase>().productDao()) }
            }
        }
    }

    // Define an "ActivityRetainedScope" for MainActivity
    // This scope survives configuration changes.
    activityRetainedScope<MainActivity> {
        // This scope can access dependencies from its parent (the root scope)
        single { MainRepository(get(), get<AppDatabase>().productDao()) }

        // Define a nested "ViewModelScope" *inside* the retained scope
        // Declaring activityScope inside of viewModelScope is prohibited by DSL
        viewModelScope<MainViewModel> {
            // This scope can access MainRepository from its parent
            viewModel { MainViewModel(get()) }
        }

        // Define another nested scope, a standard "ActivityScope"
        // This scope is destroyed with the Activity
        activityScope<MainActivity> {
            factory { MyActivityPresenter(get()) } // Can access MainRepository
        }
    }
}
```

## 3. Powerful and Predictable **Bottom UP** Navigation: From Local Scope to Global Root

```kotlin
// Get a dependency from a deeply nested scope in a type-safe and readable way
val grandChildScope = rootScope.get<MyService> {
    find of scope<ChildScopeKey>() of scope<ParentScopeKey>() of Root
}
```

### In Android

```kotlin
// Get a dependency from a deeply nested scope in a type-safe and readable way
class MainActivity :
    AppCompatActivity(),
    ScopeComponent<AndroidScopes.ActivityScope> by LazyBind() {
    private val activityScope = activityScope {
        find of activityRetainedScope<MainActivity>() of Root
    }

    private val dateFormatter by activityScope.inject<DateFormatter>()

}
```

## 4. Performance Core

The core resolution engine (get()) is reflection-free. Mole-DI uses
ConcurrentHashMap.computeIfAbsent and ThreadLocal to provide fast, thread-safe dependency resolution
without the performance overhead of reflection, making it safe for performance-critical parts of
your application.

# Getting Started

## 1. Gradle Setup

```kotlin
dependencies {
    implementation("com.github.Mole-Labs:mole-di:${version}")
    // For Android projects, include the android extension
    implementation("com.github.Mole-Labs:mole-di-android:${version}")
}
```

## 2. Application Setup

Your Application class must implement RootComponent to serve as the root of your dependency graph.

```kotlin
class MainApplication : Application(), RootComponent {

    // The single, application-wide root scope
    override val scope = DefaultScope(RootScopeQualifier)

    override fun onCreate() {
        combineToRoot(
            //define your module here
        )
    }
}
```

## 3. Defining Modules

Create modules to organize your dependency definitions. The DSL is clean and hierarchical.

```kotlin
// di/AppModule.kt
val appModule: ModuleDefinition = {
    // Root-level singletons (e.g., Application lifecycle)
    single { AppDatabase.create(get()) } // Assumes Context is declared
    single<ProductApi> { Retrofit.create(...) }
    viewModel { MainViewModel(get()) }

    // Define a scope that survives configuration changes for MainActivity
    activityScope<MainActivity> {
        single { MainRepository(get(), get<AppDatabase>().productDao()) }
    }
}
```

## 4. Injecting Dependencies in your Activity

```kotlin
// ui/MainActivity.kt
class MainActivity : AppCompatActivity(), ScopeComponent<AndroidScopes.ActivityScope> {

    // The [Scope] instance for this [ScopeComponent]
    // If you use LazyBind(), It eliminates the need for manual setup, 
    // when you call activityScope, triggers automatic scope injection to Activity
    override val scope = activityScope<MainActivity>()

    // 1. Declare properties for the scopes defined in your modules.
    //    Mole-DI will find them in the graph. `retainedScope` is the parent of `viewModelScope`.
    private val viewModelScope by viewModelScope<MainViewModel> { find of retainedScope }

    // 2. Use `autoViewModels` with the correct scope to get your ViewModel.
    private val viewModel by autoViewModels<MainViewModel>(viewModelScope)

    // 3. Inject other dependencies directly from their respective scopes.
    private val repository: MainRepository by scope.inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // You can now safely access viewModel and repository
        viewModel.products.observe(this) { /* ... */ }
    }
}

```



