package com.gobrs.async.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Entry {
    private String taskName; // 任务名称
    private String message; // 执行信息
}
