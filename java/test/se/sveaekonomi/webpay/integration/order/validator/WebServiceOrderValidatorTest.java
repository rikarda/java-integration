package se.sveaekonomi.webpay.integration.order.validator;

import static org.junit.Assert.assertEquals;

import javax.xml.bind.ValidationException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.order.VoidValidator;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder.DistributionType;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.webservice.handleorder.HandleOrder;

public class WebServiceOrderValidatorTest {
    
    private OrderValidator orderValidator;
    
    public WebServiceOrderValidatorTest() {
        orderValidator = new WebServiceOrderValidator();
    }
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    
    @Test
    public void testCheckOfIdentityClass() {
        CreateOrderBuilder order = new CreateOrderBuilder();
        order.addCustomerDetails(Item.companyCustomer()
                .setCompanyIdNumber("666666")
                .setEmail("test@svea.com")
                .setPhoneNumber(999999)
                .setIpAddress("123.123.123.123")
                .setStreetAddress("Gatan", 23)
                .setCoAddress("c/o Eriksson")
                .setZipCode("9999")
                .setLocality("Stan")); 
        orderValidator.validate(order);
    }
    @Test
    public void testFailOnMissingCountryCode() {
        String expectedMessage = "MISSING VALUE - CountryCode is required. Use function setCountryCode().\n"
                + "MISSING VALUE - OrderRows are required. Use function addOrderRow(Item.orderRow) to get orderrow setters.\n"
                + "MISSING VALUE - OrderDate is required. Use function setOrderDate().\n";
        CreateOrderBuilder order = new CreateOrderBuilder();
        order.setValidator(new VoidValidator())
        	.setClientOrderNumber("1")
        	.addCustomerDetails(Item.individualCustomer().setSsn(194609052222L));         
                
       assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailOnMissingCustomerIdentity() {
        String expectedMessage = "MISSING VALUE - Ssn is required for individual customers when countrycode is SE, NO, DK or FI. Use function setSsn().\n"
                + "MISSING VALUE - OrderRows are required. Use function addOrderRow(Item.orderRow) to get orderrow setters.\n" 
                + "MISSING VALUE - OrderDate is required. Use function setOrderDate().\n";
        CreateOrderBuilder order = new CreateOrderBuilder();
        order.addCustomerDetails(Item.individualCustomer())
        	.setValidator(new VoidValidator())
            .setClientOrderNumber("1")
            .setCountryCode(COUNTRYCODE.SE);
 
        assertEquals(expectedMessage, orderValidator.validate(order));        
    }
    
    @Test
    public void testFailOnMissingOrderRows() {
        String expectedMessage = "MISSING VALUE - OrderRows are required. Use function addOrderRow(Item.orderRow) to get orderrow setters.\n"
                + "MISSING VALUE - OrderDate is required. Use function setOrderDate().\n";
        CreateOrderBuilder order = new CreateOrderBuilder();
        order.setValidator(new VoidValidator())
            .setClientOrderNumber("1")
            .setCountryCode(COUNTRYCODE.SE)
        	.addCustomerDetails(Item.individualCustomer().setSsn(194609052222L));            

        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailOnMissingOrderRowValues() {
        String expectedMessage = "MISSING VALUE - Quantity is required in Item object. Use function Item.setQuantity().\n" 
                + "MISSING VALUE - Two of the values must be set: AmountExVat(not set), AmountIncVat(not set) or VatPercent(not set) for Orderrow. Use two functions of: setAmountExVat(), setAmountIncVat or setVatPercent().\n"
                + "MISSING VALUE - OrderDate is required. Use function setOrderDate().\n";
        CreateOrderBuilder order = new CreateOrderBuilder();
        order.setClientOrderNumber("1")
        	.addOrderRow(Item.orderRow())
        	.addCustomerDetails(Item.individualCustomer()
                .setSsn(194605092222L))
                .setCountryCode(COUNTRYCODE.SE)
            .setValidator(new VoidValidator());
         
       assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailOnMissingSsnForSeOrder() {
        String expectedMessage = "MISSING VALUE - Ssn is required for individual customers when countrycode is SE, NO, DK or FI. Use function setSsn().\n"
                + "MISSING VALUE - OrderRows are required. Use function addOrderRow(Item.orderRow) to get orderrow setters.\n"
                + "MISSING VALUE - OrderDate is required. Use function setOrderDate().\n";
        CreateOrderBuilder order = new CreateOrderBuilder();
        order.addCustomerDetails(Item.individualCustomer())
        	.setValidator(new VoidValidator())
            .setCountryCode(COUNTRYCODE.SE);        
       
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailOnMissingIdentityDataForDeOrder() {
       String expectedMessage = "MISSING VALUE - Birth date is required for individual customers when countrycode is DE. Use function setBirthDate().\n"
               + "MISSING VALUE - Name is required for individual customers when countrycode is DE. Use function setName().\n"
               + "MISSING VALUE - Street address is required for all customers when countrycode is DE. Use function setStreetAddress().\n"
               + "MISSING VALUE - Locality is required for all customers when countrycode is DE. Use function setLocality().\n"
               + "MISSING VALUE - Zip code is required for all customers when countrycode is DE. Use function setCustomerZipCode().\n"
               + "MISSING VALUE - OrderRows are required. Use function addOrderRow(Item.orderRow) to get orderrow setters.\n"
               + "MISSING VALUE - OrderDate is required. Use function setOrderDate().\n";
       CreateOrderBuilder order = new CreateOrderBuilder();
        order.setValidator(new VoidValidator())
            .setCountryCode(COUNTRYCODE.DE)
        	.addCustomerDetails(Item.individualCustomer());
               
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailOnMissingBirthDateForDeOrder() {
        String expectedMessage = "MISSING VALUE - Birth date is required for individual customers when countrycode is DE. Use function setBirthDate().\n"
                + "MISSING VALUE - OrderRows are required. Use function addOrderRow(Item.orderRow) to get orderrow setters.\n"
                + "MISSING VALUE - OrderDate is required. Use function setOrderDate().\n";
        CreateOrderBuilder order = new CreateOrderBuilder();

        order.addCustomerDetails(Item.individualCustomer()
	            .setName("Tess", "Testson")
	            .setStreetAddress("Gatan", 23)
	            .setZipCode("9999")
	            .setLocality("Stan"))         
            .setCountryCode(COUNTRYCODE.DE)
        
        	.setValidator(new VoidValidator());
        assertEquals(orderValidator.validate(order), expectedMessage);
    }
    
    @Test
    public void testFailOnMissingVatNumberForCompanyOrderDe() {
        String expectedMessage = "MISSING VALUE - Birth date is required for individual customers when countrycode is DE. Use function setBirthDate().\n"
                + "MISSING VALUE - Name is required for individual customers when countrycode is DE. Use function setName().\n"
                + "MISSING VALUE - Street address is required for all customers when countrycode is DE. Use function setStreetAddress().\n"
                + "MISSING VALUE - Locality is required for all customers when countrycode is DE. Use function setLocality().\n"
                + "MISSING VALUE - Zip code is required for all customers when countrycode is DE. Use function setCustomerZipCode().\n"
                + "MISSING VALUE - OrderRows are required. Use function addOrderRow(Item.orderRow) to get orderrow setters.\n" 
                + "MISSING VALUE - OrderDate is required. Use function setOrderDate().\n";

        CreateOrderBuilder order = new CreateOrderBuilder();
        order
            .setClientOrderNumber("1")
            .setCountryCode(COUNTRYCODE.DE)
        	.addCustomerDetails(Item.individualCustomer())
        	.setValidator(new VoidValidator());   
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailOnMissingIdentityDataForNeOrder() throws ValidationException {
        String expectedMessage = "MISSING VALUE - Initials is required for individual customers when countrycode is NL. Use function setInitials().\n"
                + "MISSING VALUE - Birth date is required for individual customers when countrycode is NL. Use function setBirthDate().\n"
                + "MISSING VALUE - Name is required for individual customers when countrycode is NL. Use function setName().\n"
                +"MISSING VALUE - Street address is required for all customers when countrycode is NL. Use function setStreetAddress().\n"
                + "MISSING VALUE - Locality is required for all customers when countrycode is NL. Use function setLocality().\n"
                + "MISSING VALUE - Zip code is required for all customers when countrycode is NL. Use function setZipCode().\n"
                + "MISSING VALUE - OrderRows are required. Use function addOrderRow(Item.orderRow) to get orderrow setters.\n" 
                + "MISSING VALUE - OrderDate is required. Use function setOrderDate().\n";
        CreateOrderBuilder order = new CreateOrderBuilder();
        order.setValidator(new VoidValidator())
            .setCountryCode(COUNTRYCODE.NL).build() 
            .addCustomerDetails(Item.individualCustomer());
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailOnMissingInitialsForNLOrder() {
        String expectedMessage = "MISSING VALUE - Initials is required for individual customers when countrycode is NL. Use function setInitials().\n"
                + "MISSING VALUE - OrderRows are required. Use function addOrderRow(Item.orderRow) to get orderrow setters.\n"
                + "MISSING VALUE - OrderDate is required. Use function setOrderDate().\n";
        CreateOrderBuilder order = new CreateOrderBuilder();
        order.addCustomerDetails(Item.individualCustomer()         
            .setBirthDate(1923, 12, 12)
            .setName("Tess", "Testson")
            .setStreetAddress("Gatan", 23)               
            .setZipCode("9999")
            .setLocality("Stan"))
        	.setCountryCode(COUNTRYCODE.NL)        
        	.setValidator(new VoidValidator());        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void succeedOnGoodValuesSe() {
        CreateOrderBuilder order = new CreateOrderBuilder();
        order.addOrderRow(Item.orderRow()
	            .setAmountExVat(5.0)
	            .setVatPercent(25)
	            .setQuantity(1))            
            .addCustomerDetails(Item.individualCustomer()
            		.setSsn(194605092222L))
            .setCountryCode(COUNTRYCODE.SE).setOrderDate("2012-05-01")        
            .setValidator(new VoidValidator());
        assertEquals("", orderValidator.validate(order));
    }
    
    @Test 
    public void testFailOnMissingOrderIdOnDeliverOrder() throws Exception {
        String expectedMessage = "MISSING VALUE - setOrderId is required.\n";
        DeliverOrderBuilder deliverOrderBuilder = WebPay.deliverOrder();
        
        deliverOrderBuilder.setTestmode();
        deliverOrderBuilder.addOrderRow(Item.orderRow()
            .setArticleNumber("1")
            .setQuantity(2)
            .setAmountExVat(100.00)
            .setDescription("Specification")
            .setName("Prod")
            .setUnit("st")
            .setVatPercent(25)
            .setDiscountPercent(0));
        HandleOrder handleOrder = deliverOrderBuilder
            .setNumberOfCreditDays(1)
            .setInvoiceDistributionType(DistributionType.Post)
            .deliverInvoiceOrder();            
   
        assertEquals(expectedMessage, handleOrder.validateOrder());  
    }
    
}
