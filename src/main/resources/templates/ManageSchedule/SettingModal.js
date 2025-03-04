    // เปิด Modal
    // document.getElementById("openModal").addEventListener("click", function() {
    //     document.getElementById("settingModal").style.display = "block";
    // });
    //
    // // ปิด Modal
    // function closeModal() {
    //     document.getElementById("settingModal").style.display = "none";
    // }
    //
    // document.getElementById("cancelBtn").addEventListener("click", closeModal);
    //
    // // ปิด Modal เมื่อกดนอก Modal
    // window.onclick = function(event) {
    //     let modal = document.getElementById("settingModal");
    //     if (event.target === modal) {
    //         closeModal();
    //     }
    // };
    //
    // // รายชื่อห้อง
    // const rooms = ["IT321", "IT322", "IT323", "IT324", "IT325", "IT221", "IT222", "IT223"];
    // let selectedRooms = new Set(); // ✅ เก็บค่าห้องที่เลือกไว้
    //
    // function toggleDropdown() {
    //     let dropdown = document.getElementById("dropdown-content");
    //     dropdown.style.display = dropdown.style.display === "block" ? "none" : "block";
    // }
    //
    // function createRooms(filteredRooms = rooms) {
    //     let roomList = document.getElementById("room-list");
    //     roomList.innerHTML = "";
    //
    //     filteredRooms.forEach(room => {
    //         let label = document.createElement("label");
    //         let checkbox = document.createElement("input");
    //         checkbox.type = "checkbox";
    //         checkbox.value = room;
    //         checkbox.checked = selectedRooms.has(room); // ✅ คืนค่าที่เลือกไว้
    //         checkbox.onchange = function () {
    //             if (checkbox.checked) {
    //                 selectedRooms.add(room);
    //             } else {
    //                 selectedRooms.delete(room);
    //             }
    //             updateSelectedCount();
    //         };
    //
    //         label.appendChild(checkbox);
    //         label.appendChild(document.createTextNode(` ${room}`));
    //         roomList.appendChild(label);
    //         roomList.appendChild(document.createElement("br"));
    //     });
    //
    //     updateSelectedCount();
    // }
    //
    // function selectAll() {
    //     let selectAllCheckbox = document.getElementById("select-all");
    //     let checkboxes = document.querySelectorAll("#room-list input[type=checkbox]");
    //
    //     checkboxes.forEach(cb => {
    //         cb.checked = selectAllCheckbox.checked;
    //         if (cb.checked) {
    //             selectedRooms.add(cb.value);
    //         } else {
    //             selectedRooms.delete(cb.value);
    //         }
    //     });
    //
    //     updateSelectedCount();
    // }
    //
    // function updateSelectedCount() {
    //     let count = selectedRooms.size;
    //     document.getElementById("selected-text").textContent = count > 0 ? `${count} Selected` : "Select Room";
    // }
    //
    // function filterRooms() {
    //     let searchValue = document.getElementById("search").value.toLowerCase();
    //     let filteredRooms = rooms.filter(room => room.toLowerCase().includes(searchValue));
    //     createRooms(filteredRooms); // ✅ เรียกใช้ createRooms() ใหม่หลังจากค้นหา
    // }
    //
    // // ปิด dropdown เมื่อคลิกข้างนอก
    // document.addEventListener("click", function(event) {
    //     if (!event.target.closest(".dropdown")) {
    //         document.getElementById("dropdown-content").style.display = "none";
    //     }
    // });
    //
    // // โหลดรายการห้องเมื่อหน้าเว็บโหลด
    // window.onload = () => createRooms();