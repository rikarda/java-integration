package se.sveaekonomi.webpay.integration.order.handle;

import javax.xml.bind.ValidationException;

import se.sveaekonomi.webpay.integration.adminservice.GetOrdersRequest;
import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.hosted.hostedadmin.QueryTransactionRequest;
import se.sveaekonomi.webpay.integration.order.OrderBuilder;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;

/**
 * @author Kristian Grossman-Madsen
 */
public class QueryOrderBuilder extends OrderBuilder<QueryOrderBuilder>{
	
    protected PAYMENTTYPE orderType;
    private Long orderId;

    public Long getOrderId() {
        return orderId;
    }
    /** Required, invoice or payment plan only, order to get details for */
    public QueryOrderBuilder setOrderId(long orderId) {
        this.orderId = orderId;
        return this;
    }
	/** Optional, card or direct bank only -- alias for setOrderId */
	public QueryOrderBuilder setTransactionId( Long transactionId) {        
	  return setOrderId( transactionId );
	}   
	/** @deprecated */
	public QueryOrderBuilder setTransactionId( String transactionId) {        
	  return setOrderId( Long.parseLong(transactionId) );
	}   
	
	public PAYMENTTYPE getOrderType() {
		return this.orderType;
	} 
    
	public QueryOrderBuilder(ConfigurationProvider config) {
		this.config = config;
	}
        
	public QueryTransactionRequest queryCardOrder() {			
		// validate request and throw exception if validation fails
        String errors = validateQueryCardOrder();        
        if (!errors.equals("")) {
            throw new SveaWebPayException("Validation failed", new ValidationException(errors));
        } 
				
		QueryTransactionRequest request = (QueryTransactionRequest) new QueryTransactionRequest(this.getConfig())
			.setTransactionId( Long.toString(this.getOrderId()) )
			.setCountryCode( this.getCountryCode() )
		;
		return request;
	}	
	
	public QueryTransactionRequest queryDirectBankOrder() {			
		// validate request and throw exception if validation fails
        String errors = validateQueryDirectBankOrder();        
        if (!errors.equals("")) {
            throw new SveaWebPayException("Validation failed", new ValidationException(errors));
        } 
				
		QueryTransactionRequest request = (QueryTransactionRequest) new QueryTransactionRequest(this.getConfig())
			.setTransactionId( Long.toString(this.getOrderId()) )
			.setCountryCode( this.getCountryCode() )
		;
		return request;
	}	
	
	// validates  queryCardOrder (querytransactionid) required attributes
    public String validateQueryCardOrder() {
        String errors = "";
        if (this.getCountryCode() == null) {
            errors += "MISSING VALUE - CountryCode is required, use setCountryCode(...).\n";
        }
        
        if (this.getOrderId() == null) {
            errors += "MISSING VALUE - OrderId is required, use setOrderId().\n";
    	}
        return errors;    
    }
    
	// validates  queryDirectBankOrder (querytransactionid) required attributes
    public String validateQueryDirectBankOrder() {
    	return validateQueryCardOrder();	// identical
    }
	
    public GetOrdersRequest queryInvoiceOrder() {
    	this.orderType = PAYMENTTYPE.INVOICE;
		// validation is done in GetOrdersRequest
		GetOrdersRequest request = new GetOrdersRequest( this );
		return request;        
    }
    
    public GetOrdersRequest queryPaymentPlanOrder() {
    	this.orderType = PAYMENTTYPE.PAYMENTPLAN;
		// validation is done in GetOrdersRequest
		GetOrdersRequest request = new GetOrdersRequest( this );
		return request;        
    } 
}
