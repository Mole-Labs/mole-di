package com.mole.android

import androidx.test.core.app.ApplicationProvider
import com.mole.android.fixture.FakeActivity
import com.mole.android.fixture.FakeApplication
import com.mole.android.fixture.FakeInvalidScopeActivity
import com.mole.android.fixture.invalidScopeModule
import com.mole.android.fixture.testModule
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = FakeApplication::class) // 👈 클래스 레벨에서 Application 지정
class AppContainerScopeTest {
    private lateinit var app: FakeApplication

    @Before
    fun setup() {
        app = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun `인스턴스를 ViewModel Scope에 등록하면 액티비티가 파괴되어도 살아남는다`() {
        // given
        app.register(app.testModule())
        val controller =
            Robolectric
                .buildActivity(FakeActivity::class.java)
                .create()
        val before = controller.get().viewModel.arg1

        // when
        controller.recreate()
        val after = controller.get().viewModel.arg1

        // then
        assert(before === after)
    }

    @Test
    fun `인스턴스를 ActivityScope에 등록하면 액티비티가 파괴될 때 해제된다`() {
        // given
        app.register(app.testModule())
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
        app.register(app.testModule())
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
    fun `다른 스코프에 등록된 객체를 가져올 수 없다`() {
        // given
        val module = app.invalidScopeModule()
        val controller =
            Robolectric
                .buildActivity(FakeInvalidScopeActivity::class.java)
        app.register(module)

        // when - then
        assertThatThrownBy {
            controller.create().get().activityArgument
        }.message().contains("등록된 스코프와 다른 스코프에서는 객체 생성이 불가능합니다")
    }
}
