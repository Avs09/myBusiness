// src/main/java/com/myBusiness/application/usecase/ScheduleReportUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.ScheduleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleReportUseCase {

    public void execute(ScheduleDto scheduleDto) {
        // TODO: Implement report scheduling logic
        // For now, just validate the input
        if (scheduleDto.getEmail() == null || scheduleDto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (scheduleDto.getFrequency() == null ||
            (!scheduleDto.getFrequency().equals("DAILY") && !scheduleDto.getFrequency().equals("WEEKLY"))) {
            throw new IllegalArgumentException("Frequency must be DAILY or WEEKLY");
        }

        // In a real implementation, this would:
        // 1. Save the schedule to database
        // 2. Set up a scheduled task (e.g., using Quartz or Spring Scheduler)
        // 3. Generate and send the report via email

        // For now, just log that the report was scheduled
        System.out.println("Report scheduled for: " + scheduleDto.getEmail() +
                          " with frequency: " + scheduleDto.getFrequency());
    }
}