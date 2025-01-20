package com.doittogether.platform.business.preset;

import com.doittogether.platform.application.global.code.ExceptionCode;
import com.doittogether.platform.application.global.exception.preset.PresetException;
import com.doittogether.platform.domain.entity.PresetCategory;
import com.doittogether.platform.domain.entity.PresetItem;
import com.doittogether.platform.infrastructure.persistence.preset.PresetCategoryRepository;
import com.doittogether.platform.infrastructure.persistence.preset.PresetItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PresetValidator {
    private final PresetCategoryRepository presetCategoryRepository;
    private final PresetItemRepository presetItemRepository;

    public void validateExistPresetCategory(Long presetCategoryId) {
        validateAndGetPresetCategory(presetCategoryId);
    }

    public void validateExistPresetItem(Long presetItemId) {
        validateAndGetPresetItem(presetItemId);
    }

    public PresetCategory validateAndGetPresetCategory(Long presetCategoryId) {
        return presetCategoryRepository.findById(presetCategoryId)
                .orElseThrow(() -> new PresetException(ExceptionCode.PRESET_CATEGORY_NOT_FOUND));
    }

    public PresetItem validateAndGetPresetItem(Long presetItemId) {
            return presetItemRepository.findById(presetItemId)
                    .orElseThrow(() -> new PresetException(ExceptionCode.PRESET_ITEM_NOT_FOUND));
    }
}
