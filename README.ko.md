![한국어](https://img.shields.io/badge/한국어-blue)
![영어](https://img.shields.io/badge/English-red)

# Mole-DI: 안드로이드를 위한 빠르고 간단한 명시적 DI 프레임워크

Mole-DI는 Kotlin을 위해 설계된 가볍고 런타임에 동작하는 의존성 주입(Dependency Injection) 프레임워크입니다.
Koin에서 많은 영감을 받았으나, **명시성**, 예측 가능성, 그리고 성능에 초점을 맞추어 설계되었습니다.

Mole-DI는 깔끔하고 직관적인 DSL을 통해 안드로이드 개발에 **명시적 스코프(Lexical Scoping)**의 강력함을 제공합니다.

# Mole-DI와 Koin의 차이점

Koin은 성숙한 프레임워크이지만, Mole-DI는 명시적이고 투명한 의존성 그래프를 강조함으로써 Koin의 "마법(Magic)" 같은 동작들을 개선하는 것을 목표로 합니다.

## 1. 정적 중첩 스코프 계층 구조

Mole-DI는 모듈 내에서 전체 스코프 계층을 미리 정의하도록 함으로써 명시성을 한 단계 더 높였습니다.
또한 의존성을 주입하는 시점에 동적으로 스코프를 생성하지 않습니다. 대신 의존성 그래프의 **정적 설계도(Static Blueprint)**를 정의하고,
필요한 스코프까지의 경로를 해석합니다. 이는 스코프 연결이 때때로 암시적이거나 흩어져 있는 것처럼 느껴질 수 있는 Koin과의 대조적입니다.

## 2. 생명주기에 안전한 DSL 제약 조건

Mole-DI의 DSL은 단순한 편의성을 넘어 올바른 설계를 강제합니다. 계층 구조는 안드로이드의 생명주기 규칙에 의해 엄격하게 관리됩니다.

### Mole-DI의 정적 및 중첩 스코프 정의 예시:

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

## 3. 강력하고 예측 가능한 **Bottom UP** 탐색

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

## 4. 성능

핵심 로직인 get() 엔진은 리플렉션(Reflection)을 사용하지 않습니다.
Mole-DI는 ConcurrentHashMap.computeIfAbsent와
ThreadLocal을 사용하여 리플렉션 오버헤드 없이 빠르고 스레드 안전한 의존성 해결을 제공합니다.
이는 성능이 중요한 애플리케이션 영역에서도 안전하게 사용할 수 있음을 의미합니다.

# 시작하기

## 1. Gradle 설정

```kotlin
dependencies {
    implementation("com.github.Mole-Labs:mole-di:${version}")
    // For Android projects, include the android extension
    implementation("com.github.Mole-Labs:mole-di-android:${version}")
}
```

## 2. Application 설정

의존성 그래프의 뿌리가 될 수 있도록 Application 클래스에 RootComponent를 구현해야 합니다.

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

## 3. 모듈 정의

의존성 정의를 조직화하기 위해 모듈을 생성합니다. DSL은 깔끔하고 계층적입니다.

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

## 4. 액티비티에서 의존성 주입하기

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



