package com.doittogether.platform.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "preset_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PresetItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long presetItemId;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "preset_category_id")
    private PresetCategory presetCategory;

    public static PresetItem of(String name, PresetCategory presetCategory) {
        PresetItem presetitem = new PresetItem();
        presetitem.name = name;
        presetitem.presetCategory = presetCategory;

        return presetitem;
    }
}
