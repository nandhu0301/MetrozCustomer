package com.smiligenceUAT1.metrozcustomer.common;

public class Constant {
    public static String PRODUCT_DETAILS_FIREBASE_TABLE = "ProductDetails";
    public  static  String SELLER_DETAILS="SellerLoginDetails";
    public static String CUSTOMER_DETAILS_FIREBASE_TABLE = "CustomerLoginDetails";
    public static String ORDER_DETAILS_FIREBASE_TABLE = "OrderDetails";
    public static String VIEW_CART_FIREBASE_TABLE = "ViewCart";


    public static String CUSTOMER_ID = "customerId";
    public static String SELLER_ID = "sellerId";

   // public static String RAZORPAY_KEY_ID="rzp_live_mWfSPGRmkxYLt8";
    //public static String RAZORPAY_SECRAT_KEY="QS2irUUJ9Y7Q0KIJ8S6mCXAe";


    //Advertisment priority number
    public static String HOME_PAGE_PRIMARY = "Home Page Primary";
    public static String HOME_PAGE_SECONDARY ="Home Page Secondary";
    public static String STORE_LIST_PRIMARY = "Store List Primary";
    public static String STORE_LIST_SECONDARY = "Store List Secondary";
    public static String ITEM_LIST_PRIMARY = "Items List Primary";
    public static String ITEM_LIST_SECONDARY = "Items List Secondary";
    public static String VIEW_CART_PRIMARY = "View Cart Primary";
    public static String VIEW_CART_SECONDARY = "View Cart Secondary";
    public static String ORDER_SUMMARY_PAGE_PRIMARY = "Order Summary Page Primary";
    public static String ORDER_SUCESS_PAGE_SECONDARY = "Order Success Page Primary";
    public static String APP_LAUNCH_POP_UP = "App Launch Pop up";
    public static String LOGIN_PAGE = "Login Page";

     public static String RAZORPAY_KEY_ID="rzp_test_XWVN0OX2MZxu0Z";
     public static String RAZORPAY_SECRAT_KEY="ToAaVQpRA5UEGzrgyTCrOWAQ";

    public static  String CATEGORY_DETAILS_TABLE="Category";
    public static String CATEGORY_IMAGE_STORAGE = "Categories/";
    public static String STORE_PINCODE = "storePincode";

    // CONSTANT For INTENT
    public static String PINCODE = "PinCode";
    public static String STORE_ID = "StoreId";
    public static String STORE_NAME = "StoreName";
    public  static  String  CATEGORY_NAME="categoryName";
    public static String CATEGORY_ID="categoryId";

    public static boolean BOOLEAN_FALSE = false;
    public static boolean BOOLEAN_TRUE = true;
    public static String REQUIRED_MSG="Required";
    public static String LAST_NAME_PATTERN = "[a-zA-Z ]+\\.?";
    public static String INVALID_FIRSTNAME_SPECIFICATION = "First Name accepts Alphabet and WhiteSpaces only!";
    public static String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+(\\.+[a-z]+)?";
   /* public  static String TAMIL_NADU_PINCODE_PATTERN="^[6]{1}[0-4]{1}[0-9]{4}$";*/
   public  static String MADURAI_PINCODE_PATTERN="^[6]{1}[0-4]{1}[0-9]{4}$";
//^[6]{1}[2]{1}[5]{1}[0-9]{3}$
    public static final int PASSWORD_LENGTH = 8;
    public static String TEXT_BLANK = "";
    public static final String PASSWORD_PATTERN = "[a-zA-Z0-9\\!\\@\\#\\$\\?\\=\\.\\*\\[\\]\\%\\^\\&\\+]{8,24}";

    public static String DATE_TIME_FORMAT_NEW = "HH:mm:ss";

    public static String INVALID_PASSWORD = "Invalid Password";
    public static String INVALID_PHONENUMBER = "Phone Number accepts 10 digit numeric value only!";
    public static String INVALID_PASSWORD_SPECIFICATION = "Password must be minimum 8 charaters and should be a combination of AlphaNumeric and Specifical characters (?=.*[@#$%^&+=!]) !!!";
    public static String RE_ENTER_PASSWORD = "Re-Enter Password";
    public static String PASSWORD_LENGTH_TOO_SHORT = "Password length should be minimum 8 charater!";
    public static String INVALID_EMAIL = "Invalid email address";
    public static String DATE_FORMAT = "MMMM dd, yyyy";
    public static String DATE_TIME_FORMAT = "MMMM dd, yyyy HH:mm:ss";
    public static String DATE_FORMAT_YYYYMD = "dd-MM-yyyy";

    /*public static String[] TAMILNADU_CITY = {"Chennai", "Coimbatore", "Madurai", "Tiruchirappalli", "Tiruppur",
            "Salem", "Erode", "Tirunelveli", "Thiruvallur", "Vellore", "Thoothukkudi", "Dindigul", "Thanjavur", "Ranipet", "Sivakasi",
            "Karur", "Udhagamandalam", "Hosur", "Nagercoil", "Kancheepuram", "Kumarapalayam", "Karaikkudi", "Neyveli",
            "Cuddalore", "Kumbakonam", "Tiruvannamalai", "Pollachi", "Rajapalayam", "Gudiyatham", "Pudukkottai",
            "Vaniyambadi", "Ambur", "Nagapattinam"};*/

    public static String[] TAMILNADU_CITY = {"Madurai"};
    public static String[] STATE_STRING = {"Tamil Nadu"};
    public static String PICKUP_DROP_TABLE = "PickupandDrop";
    public static String PHONE_NUMBER_TABLE = "PhoneNumber";

}
