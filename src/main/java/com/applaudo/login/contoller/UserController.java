    package com.applaudo.login.contoller;

    import com.applaudo.login.model.entity.User;
    import com.applaudo.login.service.UserService;
    import org.springframework.beans.factory.annotation.Autowired;

    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.regex.Matcher;
    import java.util.regex.Pattern;


    @Controller
    public class UserController{
        @Autowired
        private UserService userService;

        @GetMapping("/list_users/new")
        public String showUserRegistrationForm(Model model){
            User user = new User();
            model.addAttribute("user", user);
            return "create_users"; // Redirects to the purchase list page
        }

        @PostMapping("/list_users")
        public String saveUser(@ModelAttribute("user") User user, Model model) {
            User existingUser = userService.findUserByEmail(user.getEmail());
            if (existingUser != null) {
                model.addAttribute("emailError", true);
                return "create_users";
            }
            if (!isValidEmail(user.getEmail())) {
                model.addAttribute("invalidEmailError", true);
                return "create_users";
            }

            String password = user.getPassword();
            if (!isValidPassword(password)) {
                model.addAttribute("invalidPasswordError", true);
                return "create_users";
            }

            if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
                if (!isValidPhoneNumber(user.getPhoneNumber())) {
                    model.addAttribute("invalidPhoneNumberError", true);
                    return "create_users";
                }
                String formattedPhoneNumber = formatPhoneNumber(user.getPhoneNumber());
                user.setPhoneNumber(formattedPhoneNumber);
            } else {
                user.setPhoneNumber("");
            }

            userService.saveUser(user);
            return "redirect:/list_users";
        }

        @PostMapping("/list_users/{id}")
        public String updateUser(
                @PathVariable Long id,
                @RequestParam("password") String password,
                @ModelAttribute("user") User user,
                Model model
        ) {
            User currentUser = userService.findUserById(id);
            String currentEmail = currentUser.getEmail();

            if (!password.equals(currentUser.getPassword())) {
                model.addAttribute("passwordError", true);
                return "edit_users";
            }

            if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {

                if (!isValidPhoneNumber(user.getPhoneNumber())) {
                    model.addAttribute("invalidPhoneNumberError", true);
                    return "edit_users";
                }

                String formattedPhoneNumber = formatPhoneNumber(user.getPhoneNumber());
                currentUser.setPhoneNumber(formattedPhoneNumber);
            } else {
                currentUser.setPhoneNumber("");
            }
            currentUser.setFirsName(user.getFirsName());
            currentUser.setLastName(user.getLastName());
            currentUser.setEmail(currentEmail);
            currentUser.setPassword(user.getPassword());
            userService.updateUser(currentUser);
            return "redirect:/list_users";
        }
        @GetMapping({"/list_users/{id}"})
        public String deleteUser(@PathVariable Long id){
            userService.deleteUser(id);
            return "redirect:/list_users";
        }

        @GetMapping({"/list_users/edit/{id}"})
        public String showEditForm(@PathVariable Long id, Model model){
            User user = userService.findUserById(id);
            String phoneNumberWithoutPrefix = extractLast8Digits(user.getPhoneNumber());
            user.setPhoneNumber(phoneNumberWithoutPrefix);
            model.addAttribute("user", userService.findUserById(id));
            return "edit_users";
        }

        @GetMapping({"/list_users","/"})
        public String listUsers(Model model){
            List<User> userList = userService.listUsers();
            model.addAttribute("list_users", userList);
            return "list_users";
        }
        private boolean isValidEmail(String email) {
            String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
            Pattern pattern = Pattern.compile(emailRegex);
            Matcher matcher = pattern.matcher(email);
            return matcher.matches();
        }

        private boolean isValidPhoneNumber(String phoneNumber) {
            String phoneRegex = "\\d{8}";
            Pattern pattern = Pattern.compile(phoneRegex);
            Matcher matcher = pattern.matcher(phoneNumber);
            return matcher.matches();
        }

        private String formatPhoneNumber(String phoneNumber) {
            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                return "+503 " + phoneNumber;
            }
            return "";
        }
        private String extractLast8Digits(String phoneNumber) {
            String numericPhoneNumber = phoneNumber.replaceAll("[^0-9]", "");
            if (numericPhoneNumber.length() >= 8) {
                return numericPhoneNumber.substring(numericPhoneNumber.length() - 8);
            } else {
                return numericPhoneNumber;
            }
        }
        private boolean isValidPassword(String password) {
            return password != null && password.matches("^(?=.*[A-Za-z])(?=.*\\d).{4,}$");
        }

    }
