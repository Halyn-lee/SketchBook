// HTML 문서가 완전히 로드될 때까지 기다린 후 코드를 실행
document.addEventListener("DOMContentLoaded", function () {
    // 지정된 크기와 초기 설정으로 Fabric.js 캔버스를 생성
    let canvas;

    // 첫 번째 모달 관련 설정
    let modal1 = document.getElementById("modal1");
    let btn1 = document.getElementsByClassName("btn")[0];
    let span1 = document.getElementById("close1");

    btn1.onclick = function () {
        modal1.style.display = "block";
    }

    span1.onclick = function () {
        modal1.style.display = "none";
    }

    // 두 번째 모달 관련 설정
    let modal2 = document.getElementById("modal2");
    let btn2 = document.getElementsByClassName("btn2")[0];
    let span2 = document.getElementById("close2");

    btn2.onclick = function () {

        if (canvas == null) {
            console.log("init canvas")
            canvasInit();
        }

        modal2.style.display = "block";
    }

    span2.onclick = function () {
        modal2.style.display = "none";
    }

    function canvasInit() {
        canvas = new fabric.Canvas("canvas", {
            width: 200,
            height: 200,
            isDrawingMode: true, // 그리기 모드 활성화
            backgroundColor: "white", // 캔버스 배경색을 흰색으로 설정
        });

        // 기본 그리기 브러시 너비와 색상을 검정으로 설정
        canvas.freeDrawingBrush.width = 5;
        canvas.freeDrawingBrush.color = "black";

        // "검정" 버튼 클릭에 대한 이벤트 리스너
        document.getElementById("black").addEventListener("click", () => {
            canvas.freeDrawingBrush.width = 5;
            canvas.freeDrawingBrush.color = "black";
        });

        // "빨강" 버튼 클릭에 대한 이벤트 리스너
        document.getElementById("red").addEventListener("click", () => {
            canvas.freeDrawingBrush.width = 5;
            canvas.freeDrawingBrush.color = "red";
        });

        // "파랑" 버튼 클릭에 대한 이벤트 리스너
        document.getElementById("blue").addEventListener("click", () => {
            canvas.freeDrawingBrush.width = 5;
            canvas.freeDrawingBrush.color = "blue";
        });

        // "지우개" 버튼 클릭에 대한 이벤트 리스너
        document.getElementById("erase").addEventListener("click", () => {
            canvas.freeDrawingBrush.width = 50;
            canvas.freeDrawingBrush.color = canvas.backgroundColor;
        });

        // "첨부" 버튼 클릭에 대한 이벤트 리스너
        document.getElementById("attach").addEventListener("click", (e) => {
            console.log("첨부이벤트");
            // Canvas 내용을 데이터 URL로 변환
            let dataURL = canvas.toDataURL({format: 'png'});

            // 여러 이미지 처리
            let attachedImagesContainer = document.getElementById('attachedImagesContainer');
            let attachedImage = document.createElement('img');
            attachedImage.src = dataURL;
            attachedImage.style.maxWidth = '100%';
            attachedImage.style.height = 'auto';
            attachedImagesContainer.appendChild(attachedImage);

            // 기존 값과 새 이미지를 결합하여 hidden input 필드에 설정
            let imageDataInput = document.getElementById('imageData');
            let currentData = imageDataInput.value ? imageDataInput.value + "base64," + dataURL.split("base64,")[1] : dataURL;
            imageDataInput.value = currentData;

            // 두 번째 모달을 close
            modal2.style.display = 'none';
            canvas.dispose();
            canvas = null;
            console.log(canvas);
            e.target.removeEventListener("click");
        });
    }

    document.body.addEventListener('click', function(e) {
        if (e.target.classList.contains('modifybtn')) {
            let postId = e.target.getAttribute('data-post-id'); // 게시글 ID 추출
            let imageId = e.target.getAttribute('data-image-id'); // 이미지 ID 추출
            let modalId = `modal-container-${postId}`;
            let modal = document.getElementById(modalId);
            let modifyBtn2Id = `modifybtn2-${postId}`;
            let modifyBtn2 = document.getElementById(modifyBtn2Id);
            let deleteBtnId = `deletebtn-${postId}`;
            let deleteBtn = document.getElementById(deleteBtnId);
            let closeBtnId = `close-${postId}`
            let closeBtn = document.getElementById(closeBtnId);

            if (!modal) {
                console.error(`Modal with id ${modalId} not found.`);
                return;
            }

            modal.style.display = "block";

            if (deleteBtn) {
                deleteBtn.onclick = function () {
                    deleteBtn.style.display = 'none';

                    // 각 이미지에 체크박스 생성
                    let images = modal.querySelectorAll("img");
                    images.forEach(image => {
                        let imageId = image.id.split('-')[1];
                        if (!document.getElementById(`checkbox-${imageId}`)) {
                            let checkbox = document.createElement("input");
                            checkbox.type = "checkbox";
                            checkbox.id = `checkbox-${imageId}`;
                            image.after(checkbox);
                        }
                    });

                    // 모달창 안에 삭제 확인 버튼 생성
                    let modalContent = modal.querySelector('.modal-content');
                    let confirmDeleteBtn = document.createElement("button");
                    confirmDeleteBtn.innerText = "확인";
                    confirmDeleteBtn.id = 'confirm-delete-btn';
                    modalContent.appendChild(confirmDeleteBtn); // modal-content 안에 생성

                    // 이미지 삭제 확인 버튼 핸들러
                    confirmDeleteBtn.onclick = function() {
                        let selectedImageIds = Array.from(images).filter(img => document.getElementById(`checkbox-${img.id.split('-')[1]}`).checked).map(img => img.id.split('-')[1]);

                        if (selectedImageIds.length > 0) {
                            deleteSelectedImages(selectedImageIds);
                        } else {
                            alert("이미지가 선택되지 않았어요.");
                        }
                    };
                };
            } else {
                console.error(` ${deleteBtnId} 없음`);
            }

            function deleteSelectedImages(imageIds) {
                const xhr = new XMLHttpRequest();
                xhr.open("POST", "/images/delete", true);
                xhr.setRequestHeader("Content-Type", "application/json");
                xhr.onreadystatechange = function () {
                    if (xhr.readyState === 4 && xhr.status === 200) {
                        const response = JSON.parse(xhr.responseText);
                        if (response.success) {
                            // 응답 결과로 화면에서 제거
                            imageIds.forEach(id => {
                                let checkbox = document.getElementById(`checkbox-${id}`);
                                if (checkbox) checkbox.remove();
                                let image = document.getElementById(`image-${id}`);
                                if (image) image.remove();
                            });
                        } else {
                            alert("이미지 삭제 실패");
                        }
                    }
                };
                xhr.send(JSON.stringify({ selectedImageIds: imageIds }));
            }

            closeBtn.onclick = function () {
                modal.style.display = "none";
            }

        }
    });
});