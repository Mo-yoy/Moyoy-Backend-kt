package com.moyoy.infra

import org.springframework.context.annotation.DeferredImportSelector
import org.springframework.core.type.AnnotationMetadata

class MoyoyConfigImportSelector : DeferredImportSelector {
    override fun selectImports(metadata: AnnotationMetadata): Array<String> {
        return getValues(metadata)
            .map { it.configClass.java.name }
            .toTypedArray()
    }

    private fun getValues(metadata: AnnotationMetadata): Array<MoyoyConfigGroup> {
        val attributes = metadata.getAnnotationAttributes(EnableMoyoyConfig::class.java.name)

        @Suppress("UNCHECKED_CAST")
        return attributes?.get("value") as? Array<MoyoyConfigGroup> ?: emptyArray()
    }
}
