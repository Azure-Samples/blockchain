package net.corda.reflections.annotations

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION,
        AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.EXPRESSION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Description(val text: String)
