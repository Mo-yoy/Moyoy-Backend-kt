package com.moyoy.infra

import org.springframework.context.annotation.Import

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Import(MoyoyConfigImportSelector::class)
annotation class EnableMoyoyConfig(
    val value: Array<MoyoyConfigGroup> = []
)
