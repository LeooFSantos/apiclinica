package apiclinica.email.api.dto;

public class EmailResponse {
  private boolean sent;
  private String message;

  public EmailResponse() {}

  public EmailResponse(boolean sent, String message) {
    this.sent = sent;
    this.message = message;
  }

  public boolean isSent() { return sent; }
  public void setSent(boolean sent) { this.sent = sent; }

  public String getMessage() { return message; }
  public void setMessage(String message) { this.message = message; }
}
