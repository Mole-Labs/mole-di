package com.mole.android

import androidx.test.core.app.ApplicationProvider
import com.mole.android.dsl.module.activityRetainedScope
import com.mole.android.dsl.module.activityScope
import com.mole.android.dsl.module.viewModel
import com.mole.android.dsl.module.viewModelScope
import com.mole.android.fixture.Child1
import com.mole.android.fixture.Child2
import com.mole.android.fixture.Child3
import com.mole.android.fixture.FakeActivity
import com.mole.android.fixture.FakeActivityLazyBind
import com.mole.android.fixture.FakeActivityNestedScope
import com.mole.android.fixture.FakeApplication
import com.mole.android.fixture.FakeViewModelScopeActivity
import com.mole.android.fixture.TestViewModel
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = FakeApplication::class)
class AndroidScopeTest {
    private lateinit var app: FakeApplication

    @Before
    fun setup() {
        app = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun `if an instance is registered in the ViewModel Scope, it survives even if the activity is destroyed`() {
        // given
        app.combineToRoot(
            {
                viewModelScope<TestViewModel> {
                    single { Child1() }
                    viewModel<TestViewModel> { TestViewModel(get()) }
                }
            },
        )
        val controller =
            Robolectric
                .buildActivity(FakeViewModelScopeActivity::class.java)
                .create()
        val before = controller.get().arg1

        // when
        controller.recreate()
        val after = controller.get().arg1

        // then
        assert(before === after)
    }

    @Test
    fun `if an instance is registered in the ActivityScope, it is released when the activity is destroyed`() {
        // given
        app.combineToRoot({
            activityScope<FakeActivity> {
                single { Child2() }
            }

            activityRetainedScope<FakeActivity> {
                single { Child3() }
            }
        })
        val controller =
            Robolectric
                .buildActivity(FakeActivity::class.java)
                .create()
        val before = controller.get().activityArgument

        // when
        controller.recreate()
        val after = controller.get().activityArgument

        // then
        assert(before !== after)
    }

    @Test
    fun `if an instance is registered in the ActivityRetainedScope, it survives even if the activity is destroyed`() {
        // given
        app.combineToRoot({
            activityScope<FakeActivity> {
                single { Child2() }
            }

            activityRetainedScope<FakeActivity> {
                single { Child3() }
            }
        })
        val controller =
            Robolectric
                .buildActivity(FakeActivity::class.java)
                .create()
        val before = controller.get().activityRetainedArgument

        // when
        controller.recreate()
        val after = controller.get().activityRetainedArgument

        // then
        assert(before === after)
    }

    @Test
    fun `ActivityScope can be nested in ActivityRetainedScope`() {
        // given
        app.combineToRoot({
            activityRetainedScope<FakeActivityNestedScope> {
                activityScope<FakeActivityNestedScope> {
                    single { Child2() }
                }
                single { Child3() }
            }
        })
        val controller =
            Robolectric
                .buildActivity(FakeActivityNestedScope::class.java)
                .create()
        val before = controller.get().activityRetainedArgument

        // when
        controller.recreate()
        val after = controller.get().activityRetainedArgument

        // then
        assert(before === after)
    }

    @Test
    fun `Using LazyBind creates a scope when the activity extension function is called`() {
        // given
        app.combineToRoot({
            activityScope<FakeActivityLazyBind> {
                single { Child2() }
            }
        })

        val controller =
            Robolectric
                .buildActivity(FakeActivityLazyBind::class.java)
                .create()
        val before = controller.get().activityArgument

        // when
        controller.recreate()
        val after = controller.get().activityArgument

        // then
        assert(before !== after)
    }
}
