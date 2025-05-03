package com.example.printapp.controller;

import com.example.printapp.model.PrintRequest;
import com.example.printapp.repository.PrintRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PrintRequestController {

    @Autowired
    private PrintRequestRepository printRequestRepository;

    // This method handles the GET request to show the form where users can enter the print request details
    @GetMapping("/request")
    public String showRequestForm(Model model) {
        model.addAttribute("printRequest", new PrintRequest());
        return "request";  // Returns the request.html page
    }

    // This method handles the form submission and saves the print request to the database
    @PostMapping("/submitRequest")
    public String submitRequest(@ModelAttribute PrintRequest printRequest, Model model) {
        if (printRequest != null && isValidPrintRequest(printRequest)) {
            String color = printRequest.getColor();

            // Calculate amount based on color and pages
            double amount = calculateAmount(printRequest);
            printRequest.setAmount(amount);

            // Display the details
            model.addAttribute("printRequest", printRequest);

            // Save the printRequest to the database
            printRequestRepository.save(printRequest);

            return "submit";  // Redirect to submit.html (or your confirmation page)
        } else {
            model.addAttribute("error", "Invalid Print Request. Please ensure all fields are filled correctly.");
            return "error";  // Redirect to the error page
        }
    }

    // This method handles the redirect to confirmation page after a successful submission
    @GetMapping("/confirmation")
    public String showConfirmation(Model model) {
        model.addAttribute("message", "Your print request has been successfully submitted! Please hand over the money to the admin when you collect your documents.");
        return "confirmation";  // Redirect to confirmation.html page
    }

    // Helper method to calculate the amount based on color and number of pages
    private double calculateAmount(PrintRequest printRequest) {
        double amount = 0;
        if ("color".equalsIgnoreCase(printRequest.getColor())) {
            amount = 3 * printRequest.getPages();  // 3 rs per color page
        } else if ("bw".equalsIgnoreCase(printRequest.getColor())) {
            amount = 2 * printRequest.getPages();  // 2 rs per black and white page
        }
        return amount;
    }

    // Helper method to validate print request
    private boolean isValidPrintRequest(PrintRequest printRequest) {
        return printRequest.getName() != null && !printRequest.getName().isEmpty()
            && printRequest.getDocumentName() != null && !printRequest.getDocumentName().isEmpty()
            && printRequest.getColor() != null && !printRequest.getColor().isEmpty()
            && printRequest.getSided() != null && !printRequest.getSided().isEmpty()
            && printRequest.getPages() > 0;
    }
}
