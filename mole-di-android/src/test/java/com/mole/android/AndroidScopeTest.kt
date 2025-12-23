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
    fun `인스턴스를 ViewModel Scope에 등록하면 액티비티가 파괴되어도 살아남는다`() {
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
    fun `인스턴스를 ActivityScope에 등록하면 액티비티가 파괴될 때 해제된다`() {
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
    fun `인스턴스를 ActivityRetainedScope에 등록하면 액티비티가 파괴되도 살아남는다`() {
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
    fun `ActivityScope를 ActivityRetainedScope에 중첩 등록할 수 있다`() {
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
    fun `LazyBind를 사용하면 액티비티 확장함수를 호출할 때 스코프가 생성된다`() {
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
