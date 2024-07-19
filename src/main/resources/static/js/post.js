let canvas;

// HTML 문서가 완전히 로드될 때까지 기다린 후 코드를 실행
document.addEventListener("DOMContentLoaded", function () {
    // 지정된 크기와 초기 설정으로 Fabric.js 캔버스를 생성
    canvas = new fabric.Canvas("canvas", {
        width: 400,
        height: 400,
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
        var dataURL = canvas.toDataURL({format: 'png'});

        // 첫 번째 모달에 이미지를 표시
        var attachedImage = document.getElementById('attachedImage');
        attachedImage.src = dataURL;
        attachedImage.style.display = 'block';

        // 이미지 데이터 URL을 숨겨진 input 필드에 설정
        document.getElementById('imageData').value = dataURL;

        // 두 번째 모달을 close
        modal2.style.display = 'none';
    });

// 첫 번째 모달 관련 설정
var modal1 = document.getElementById("modal1");
var btn1 = document.getElementsByClassName("btn")[0];
var span1 = document.getElementById("close1");

btn1.onclick = function () {
    modal1.style.display = "block";
}

span1.onclick = function () {
    modal1.style.display = "none";
}

// 두 번째 모달 관련 설정
var modal2 = document.getElementById("modal2");
var btn2 = document.getElementsByClassName("btn2")[0];
var span2 = document.getElementById("close2");

btn2.onclick = function () {
    modal2.style.display = "block";
}

span2.onclick = function () {
    modal2.style.display = "none";
}
});

// // Modal 영역 밖 클릭시 Modal close
// window.onclick = function (event) {
//     if (event.target == modal1) {
//         modal1.style.display = "none";
//     } else if (event.target == modal2) {
//         modal2.style.display = "none";
//     }
// };