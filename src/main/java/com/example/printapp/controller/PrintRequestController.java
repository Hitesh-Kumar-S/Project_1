package com.example.printapp.controller;

import com.example.printapp.model.PrintRequest;
import com.example.printapp.repository.PrintRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@SessionAttributes("printRequest") // Retain printRequest in session between requests
public class PrintRequestController {

    private static final double COLOR_RATE = 3.0;
    private static final double BW_RATE = 2.0;

    @Autowired
    private PrintRequestRepository printRequestRepository;

    // Initialize printRequest in session if not present
    @ModelAttribute("printRequest")
    public PrintRequest getPrintRequest() {
        return new PrintRequest();
    }

    // Show the request form
    @GetMapping("/request")
    public String showRequestForm() {
        return "request"; // Uses session-backed model attribute
    }

    // Handle form submission and show submit.html (summary)
    @PostMapping("/submitRequest")
    public String submitRequest(@ModelAttribute("printRequest") PrintRequest printRequest, Model model) {
        sanitizeInput(printRequest);

        if (printRequest != null && isValidPrintRequest(printRequest)) {
            double amount = calculateAmount(printRequest);
            printRequest.setAmount(amount);
            model.addAttribute("printRequest", printRequest);
            return "submit";  // Show submit.html for review (no saving yet)
        } else {
            model.addAttribute("error", "Invalid Print Request. Please ensure all fields are filled correctly.");
            return "request"; // Return to request form with error
        }
    }

    // Handle confirmation (saving) when user clicks "Proceed"
    @PostMapping("/confirmRequest")
    public String confirmRequest(@ModelAttribute("printRequest") PrintRequest printRequest,
                                 Model model,
                                 SessionStatus sessionStatus) {
        printRequestRepository.save(printRequest);
        sessionStatus.setComplete(); // Clear session attribute
        model.addAttribute("message", "Your print request has been successfully submitted! Please hand over the money to the admin when you collect your documents.");
        return "confirmation";
    }

    // Calculate amount based on color and pages
    private double calculateAmount(PrintRequest printRequest) {
        return "color".equalsIgnoreCase(printRequest.getColor())
                ? COLOR_RATE * printRequest.getPages()
                : BW_RATE * printRequest.getPages();
    }

    // Validate form fields
    private boolean isValidPrintRequest(PrintRequest printRequest) {
        return printRequest.getName() != null && !printRequest.getName().isEmpty()
            && printRequest.getDocumentName() != null && !printRequest.getDocumentName().isEmpty()
            && printRequest.getColor() != null && !printRequest.getColor().isEmpty()
            && printRequest.getSided() != null && !printRequest.getSided().isEmpty()
            && printRequest.getPages() > 0;
    }

    // Trim inputs to avoid errors from extra spaces
    private void sanitizeInput(PrintRequest printRequest) {
        if (printRequest.getName() != null)
            printRequest.setName(printRequest.getName().trim());
        if (printRequest.getDocumentName() != null)
            printRequest.setDocumentName(printRequest.getDocumentName().trim());
        if (printRequest.getColor() != null)
            printRequest.setColor(printRequest.getColor().trim());
        if (printRequest.getSided() != null)
            printRequest.setSided(printRequest.getSided().trim());
    }
}

