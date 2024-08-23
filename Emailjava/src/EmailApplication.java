public class EmailApplication {
    public static void main(String[] args) {
        // Tài khoản email thứ nhất
        String email1 = "email1@gmail.com";
        String password1 = "password1";
        
        // Tài khoản email thứ hai
        String email2 = "email2@gmail.com";
        String password2 = "password2";
        
        // Tạo giao diện GUI cho cả hai tài khoản email
        EmailGUI emailGUI1 = new EmailGUI("hvthao.ic.tn20@gmail.com", "wtdi eznn bbzd jtfh");
        emailGUI1.setVisible(true);
        
        EmailGUI emailGUI2 = new EmailGUI("trungconta@gmail.com", "cxzr ahzt eygb sume");
        emailGUI2.setVisible(true);
    }
}
