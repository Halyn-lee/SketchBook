let canvas;

// HTML 문서가 완전히 로드될 때까지 기다린 후 코드를 실행
document.addEventListener("DOMContentLoaded", function () {
    // 지정된 크기와 초기 설정으로 Fabric.js 캔버스를 생성
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
    document.getElementById("attach").addEventListener("click", () => {
        // Canvas 내용을 데이터 URL로 변환
        let dataURL = canvas.toDataURL({ format: 'png' });

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
    });

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
        modal2.style.display = "block";
    }

    span2.onclick = function () {
        modal2.style.display = "none";
    }
});