package com.gyanesh.pocketManager.service;

import com.gyanesh.pocketManager.dto.ExpenseDto;
import com.gyanesh.pocketManager.entity.CategoryEntity;
import com.gyanesh.pocketManager.entity.ExpenseEntity;
import com.gyanesh.pocketManager.entity.ProfileEntity;
import com.gyanesh.pocketManager.repository.CategoryRepository;
import com.gyanesh.pocketManager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ExpenseService expenseService;
    private final ProfileRepository profileRepo;
    private final EmailService emailService;
    private final CategoryRepository categoryRepo;

    @Value("${pocket.manager.url}")
    private String frontendUrl;

    @Scheduled(cron ="0 30 21 * * *", zone = "IST")
    public void sendDailyExpenseReminder(){
        log.info("Job Started: send daily reminder notification");
        List<ProfileEntity> profiles = profileRepo.findAll();

        for(ProfileEntity profile: profiles){

            String body =
                    "<div style='background-color: #ffffff; padding: 20px; border-radius: 10px; " +
                            "max-width: 500px; margin: auto; box-shadow: 0 2px 8px rgba(0,0,0,0.1);'>" +

                            "<h2 style='color: #333333; margin-bottom: 15px;'>" +
                            "Daily Reminder" +
                            "</h2>" +

                            "<p style='color: #555555; font-size: 16px; line-height: 1.6;'>" +
                            "Hi " + profile.getFullName() + ",<br><br>" +

                            "This is a friendly reminder to upload your daily transactions " +
                            "to keep your records updated and track your expenses effectively." +
                            "<br><br>" +

                            "Thank you for using Pocket Manager!" +
                            "</p>" +

                            "<p style='margin-top: 25px; color: #777777; font-size: 14px;'>" +
                            "Best Regards,<br>" +
                            "Pocket Manager Team" +
                            "</p>" +

                            "</div>";

            emailService.sendEmail(profile.getEmail(), "Daily Reminder: Add your daily transaction", body);
        }

    }

    @Scheduled(cron = "0 30 23 * * *", zone = "IST")
    public void dailyExpenseSummary() {

        log.info("Initiating daily expense summary scheduler");

        List<ProfileEntity> profiles = profileRepo.findAll();

        for (ProfileEntity profile : profiles) {

            List<ExpenseDto> totalExpenses =
                    expenseService.getExpensesForUserOnDate(
                            profile.getId(),
                            LocalDate.now()
                    );

            if (!totalExpenses.isEmpty() && profile.getIsActive()) {

                StringBuilder table = new StringBuilder();

                table.append("<table style='border-collapse:collapse;width:100%;font-family:Arial,sans-serif;'>");

                // Header
                table.append("<tr style='background-color:#f2f2f2;'>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>S.No</th>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>Name</th>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>Category</th>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>Amount</th>")
                        .append("</tr>");

                int i = 1;

                for (ExpenseDto expense : totalExpenses) {

                    CategoryEntity category = categoryRepo.findByIdAndProfileId(expense.getCategoryId(), profile.getId()).orElse(null);

                    assert category != null;
                    table.append("<tr>")
                            .append("<td style='border:1px solid #ddd;padding:8px;'>")
                            .append(i++)
                            .append("</td>")

                            .append("<td style='border:1px solid #ddd;padding:8px;'>")
                            .append(expense.getName())
                            .append("</td>")

                            .append("<td style='border:1px solid #ddd;padding:8px;'>")
                            .append(expense.getCategoryId()!=null ? expense.getCategoryName() :"N/A")
                            .append("</td>")

                            .append("<td style='border:1px solid #ddd;padding:8px;'>₹")
                            .append(expense.getAmount())
                            .append("</td>")

                            .append("</tr>");
                }

                table.append("</table>");

                String htmlTable = table.toString();

                emailService.sendEmail(profile.getEmail(), "Daily Expense Summary: "+LocalDate.now(), htmlTable);

            }
        }
    }

}
