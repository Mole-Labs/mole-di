package com.mole.core.annotation

@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "For internal Mole DI module use only. May cause unexpected side effects if used externally.",
)
@Retention(AnnotationRetention.BINARY)
annotation class MoleInternalApi
