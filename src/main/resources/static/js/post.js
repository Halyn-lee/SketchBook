// HTML 문서가 완전히 로드될 때까지 기다린 후 코드를 실행
document.addEventListener("DOMContentLoaded", function () {
    let canvas;
    let canvasIsModify = document.getElementById("canvas").dataset.ismodify === "true"; // 수정용 여부 확인

    // modal1 : 게시글 작성 모달
    let modal1 = document.getElementById("modal1");
    let btn1 = document.getElementsByClassName("btn")[0];
    let span1 = document.getElementById("close1");

    btn1.onclick = function () {
        modal1.style.display = "block";
    }

    span1.onclick = function () {
        modal1.style.display = "none";
    }

    // modal2 : 그림 그리기 모달
    let modal2 = document.getElementById("modal2");
    let btn2 = document.getElementsByClassName("btn2")[0];
    let span2 = document.getElementById("close2");

    btn2.onclick = function () {
        if (!canvas) {
            console.log("init canvas");
            canvasInit();
        }
        modal2.style.display = "block";
        canvasIsModify = false;
    }

    span2.onclick = function () {
        modal2.style.display = "none";
    }

    function canvasInit() {
        canvas = new fabric.Canvas("canvas", {
            width: 800,
            height: 600,
            isDrawingMode: true,
            backgroundColor: "white",
        });

        // 기본 그리기 브러시 너비와 색상을 검정으로 설정
        canvas.freeDrawingBrush.width = 5;
        canvas.freeDrawingBrush.color = "black";

        // 버튼 클릭 이벤트 리스너
        
        // 지우개
        document.getElementById("erase").addEventListener("click", () => {
            canvas.freeDrawingBrush.width = parseInt(document.getElementById('eraser-size').value, 10);
            canvas.freeDrawingBrush.color = canvas.backgroundColor;
        });

        // 브러시 크기 조절
        document.getElementById('brush-size').addEventListener('input', (e) => {
            canvas.freeDrawingBrush.width = parseInt(e.target.value, 10);
        });

        // 지우개 크기 조절
        document.getElementById('eraser-size').addEventListener('input', (e) => {
            canvas.freeDrawingBrush.width = parseInt(e.target.value, 10);
        });

        // 색상 선택기
        document.getElementById('color-picker').addEventListener('input', (e) => {
            canvas.freeDrawingBrush.color = e.target.value;
        });

        // "첨부" 버튼 클릭에 대한 이벤트 리스너
        document.getElementById("attach").addEventListener("click", (e) => {
            console.log("첨부이벤트");
            console.log("false 떠야 됨" + canvasIsModify);

            // Canvas 내용을 데이터 URL로 변환
            let dataURL = canvas.toDataURL({format: 'png'});
            let imageDataInput;
            let attachedImagesContainer;
            let attachedImage = document.createElement('img');
            attachedImage.src = dataURL;
            attachedImage.style.maxWidth = '40%';
            attachedImage.style.height = 'auto';

            if (!canvasIsModify) {
                imageDataInput = document.getElementById('imageData');
                attachedImagesContainer = document.getElementById('attachedImagesContainer');
            } else {
                imageDataInput = document.getElementById('postImageData'); // 수정 캔버스
                attachedImagesContainer = document.getElementById('postAttachedImagesContainer');
            }
            attachedImagesContainer.appendChild(attachedImage);

            let currentData = imageDataInput.value ? imageDataInput.value + "base64," + dataURL.split("base64,")[1] : dataURL;
            imageDataInput.value = currentData;

            modal2.style.display = 'none';
            canvas.dispose();
            canvas = null;
            console.log(canvas);
            e.target.removeEventListener("click");
        });

        // "이미지 추가" 버튼 클릭 이벤트 리스너
        document.getElementById("add-image").addEventListener("click", () => {
            document.getElementById("filereader").click(); // 파일 선택기 열기
        });

        // 파일 선택기 변경 이벤트 리스너
        document.getElementById("filereader").addEventListener("change", (event) => {
            const file = event.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    const img = new Image();
                    img.onload = function() {
                        const fabricImage = new fabric.Image(img, {
                            left: 100,
                            top: 100,
                            scaleX: 1,
                            scaleY: 1
                        });

                        canvas.add(fabricImage);
                        canvas.setActiveObject(fabricImage);
                        canvas.renderAll();

                        // 사이즈 조정용
                        fabricImage.setControlsVisibility({
                            mt: true, // middle top
                            mb: true, // middle bottom
                            ml: true, // middle left
                            mr: true, // middle right
                            bl: true, // bottom left
                            br: true, // bottom right
                            tr: true, // top right
                            tl: true  // top left
                        });

                        // 크기 조절하는 동안에는 브러쉬모드 off
                        canvas.isDrawingMode = false;
                        
                        canvas.renderAll();
                    }
                    img.src = e.target.result;
                }
                reader.readAsDataURL(file);
            }
        });

        document.getElementById('color-picker').addEventListener('click', () => {
            canvas.selection = false; // 선택 모드 비활성화
            canvas.isDrawingMode = true;
            document.getElementById('color-picker').enabled = true;
        });

        // 요소 선택 모드 버튼 클릭
        document.getElementById('select-element').addEventListener('click', () => {
            canvas.selection = true; // 선택 모드 활성화
            canvas.isDrawingMode = false; // 드로잉 모드 비활성화
            document.getElementById('color-picker').disabled = false; // 선택 모드일 때 색상 선택기 활성화
        });

        // 삭제 버튼 클릭 시 선택된 요소 삭제
        document.getElementById("delete-canvas-element").addEventListener("click", () => {
            // 선택된 모든 개체 get
            const activeObjects = canvas.getActiveObjects();

            if (activeObjects.length > 0) {
                activeObjects.forEach(obj => {
                    canvas.remove(obj);
                });

                // 선택된 객체 모두 삭제한 뒤 렌더링
                canvas.discardActiveObject();
                canvas.renderAll();
            } else {
                alert("삭제할 요소가 선택되지 않았어요.");
            }
        });
    }


    // 게시물 수정 버튼 이벤트 리스너
    document.body.addEventListener('click', function (e) {
        if (e.target.classList.contains('modifybtn')) {
            let postId = e.target.getAttribute('data-post-id');
            let modal = document.querySelector("#postModalContainer");
            let modalImageContainer = document.querySelector(".post-image-list");
            let imageTags = e.target.parentElement.nextElementSibling.querySelectorAll("img");
            modalImageContainer.innerHTML = "";

            let postContent = document.querySelector("#post-container-" + postId + " .fw-bold.fs-5").getAttribute('data-content');
            let textarea = modal.querySelector("textarea");
            textarea.value = postContent;

            for (let image of imageTags) {
                const image_no = image.dataset.id;
                const image_src = image.src;

                let newImageTag = document.createElement("img");
                newImageTag.src = image_src;
                newImageTag.dataset.id = image_no;
                newImageTag.classList = ["d-block", "w-10"];
                newImageTag.id = `image-${image_no}`;
                modalImageContainer.appendChild(newImageTag)
            }

            let deleteBtn = document.getElementById("postDelBtn");
            let addBtn = document.getElementById("postAddBtn");
            let closeBtn = document.querySelector("#postModalClose");
            let submitBtn = document.querySelector("#postModBtn");
            let modForm = document.querySelector("#modForm");

            modal.style.display = "block";

            if (deleteBtn) {
                deleteBtn.onclick = function () {
                    deleteBtn.style.display = 'none';

                    // 각 이미지에 체크박스 생성
                    let images = modalImageContainer.querySelectorAll("img");
                    let imageCheckboxes = [];
                    images.forEach(image => {
                        let imageId = image.dataset.id
                        if (!document.getElementById(`checkbox-${imageId}`)) {
                            let checkbox = document.createElement("input");
                            checkbox.type = "checkbox";
                            checkbox.id = `checkbox-${imageId}`;
                            image.after(checkbox);
                            imageCheckboxes.push(checkbox);
                        }
                    });

                    let modalContent = modal.querySelector('.modal-content');
                    let confirmDeleteBtn = document.createElement("button");
                    confirmDeleteBtn.innerText = "확인";
                    confirmDeleteBtn.id = 'confirm-delete-btn';
                    modalContent.appendChild(confirmDeleteBtn);

                    // 이미지 체크 박스 선택 후 삭제 확인 버튼 핸들러
                    confirmDeleteBtn.onclick = function () {
                        let selectedImageIds = [];
                        for (let check of imageCheckboxes) {
                            if (check.checked) {
                                selectedImageIds.push(check.previousSibling.dataset.id);
                            }
                        }

                        if (selectedImageIds.length > 0) {
                            deleteSelectedImages(selectedImageIds);
                        } else {
                            alert("이미지가 선택되지 않았어요.");
                        }
                    };

                    // 취소 버튼
                    let cancelBtn = document.createElement("button")
                    cancelBtn.type = "button"
                    cancelBtn.innerText = "취소"
                    modalContent.appendChild(cancelBtn);
                    cancelBtn.addEventListener("click", function (e) {
                        for (let check of imageCheckboxes) {
                            check.remove();
                        }
                        cancelBtn.remove();
                        confirmDeleteBtn.remove();
                        document.querySelector("#postDelBtn").style.display = 'block';
                        // e.target.removeEventListener("click");
                    })
                };
            }

            // 게시글 수정 모달 내 이미지 삭제
            function deleteSelectedImages(imageIds) {
                const xhr = new XMLHttpRequest();
                xhr.open("POST", "/images/delete", true);
                xhr.setRequestHeader("Content-Type", "application/json");
                xhr.onreadystatechange = function () {
                    if (xhr.readyState === 4 && xhr.status === 200) {
                        const response = JSON.parse(xhr.responseText);
                        if (response.success) {
                            imageIds.forEach(id => {
                                let checkbox = document.getElementById(`checkbox-${id}`);
                                if (checkbox) checkbox.remove();
                                let image = document.getElementById(`image-${id}`);
                                console.log(image, id)
                                if (image) image.remove();
                            });
                        } else {
                            alert("이미지 삭제 실패");
                        }
                    }
                };
                xhr.send(JSON.stringify({selectedImageIds: imageIds}));
            }

            closeBtn.onclick = function () {
                modal.style.display = "none";
            }

            addBtn.onclick = function () {
                if (canvas == null) {
                    console.log("init canvas")
                    canvasInit();
                }
                modal2.style.display = "block";
                canvasIsModify = true;
                console.log("캔버스 체크" + canvas);
            }

            // 게시물 수정 완료 버튼을 눌렀을 때 제출할 form의 action attribute 추가 및 submit 처리
            submitBtn.onclick = function (e) {
                e.preventDefault();
                let baseURL = "/post/modify/" + postId;
                modForm.setAttribute("action", baseURL);
                modForm.submit();
            }
        }
    });
});