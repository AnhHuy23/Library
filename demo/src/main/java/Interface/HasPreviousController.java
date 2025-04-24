package Interface;
// Cài đặt nếu cần lấy controller từ Scene trước
public interface HasPreviousController <PreviousClass> 
{
    void SetController(PreviousClass previousController); 
}