# THMap
Tìm các điểm lân cận sử dụng google map API

- Ứng dụng chạy trên điện thoại Android (test trên máy Google Nexus 4 4.4.4 Kitkat API 19) có hỗ trợ GPS và internet.
- Ứng dụng sử dụng Google map API để hiển thị map và Google place API để liệt kê các địa điểm lân cận
- Để sử dụng: Sau khi khởi chạy ứng dụng, cần phải bật GPS để xác định vị trí hiện tại, sau đó trên màn hình ứng dụng, ta bấm vào nút xác định vị trí hiện tại. Khi người dùng bấm vào button Địa điểm lân cận trên map sẽ đánh dấu các địa điểm lân cận và có 1 list các địa điểm lân cận  và thông tin (icon, thông tin chi tiết). Khi ta bấm vào 1 địa điểm trên list thì map sẽ tự động chỉnh camera zoom về vị trí đó, khi ta bấm vào điểm đánh dấu trên map sẽ hiển thị tên của địa điểm đó.
- Hạn chế:  
   + Ứng dụng chưa được tối ưu về tốc độ với lượng dữ liệu lớn.
   + Chưa có chức năng hiển thị khoảng cách từ điểm ta chọn đến vị trí hiện tại.
   + Với điều kiện mạng chập chờn thì ứng dụng sẽ bị đơ nhưng vẫn tiếp tục xử lý
- Hướng khắc phục:
  + Sử dụng thêm multi thread để tối ưu việc tải dữ liệu trên mạng về
  + Sử dụng thêm Google Map API về tìm đường và khoảng cách giữa 2 điểm trên map
