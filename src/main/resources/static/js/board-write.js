document.addEventListener("DOMContentLoaded", () => {

    const form = document.getElementById("writeForm");

    const category = document.getElementById("category");

    const title = document.getElementById("title");
    const content = document.getElementById("content");

    const titleCount = document.getElementById("titleCount");
    const contentCount = document.getElementById("contentCount");

    const titleMessage = document.getElementById("titleMessage");
    const contentMessage = document.getElementById("contentMessage");

    const fileUploadGroup =
        document.getElementById("fileUploadGroup");

    const dropZone =
        document.getElementById("dropZone");

    const fileInput =
        document.getElementById("fileInput");

    const fileSelectButton =
        document.getElementById("fileSelectButton");

    const fileList =
        document.getElementById("fileList");

    const fileCount =
        document.getElementById("fileCount");

    const totalSize =
        document.getElementById("totalSize");

    const fileError =
        document.getElementById("fileError");


    /*
     * 글자 수 제한
     */

    const MAX_TITLE_LENGTH = 10;
    const MAX_CONTENT_LENGTH = 300;


    /*
     * 파일 제한
     */

    const MAX_FILE_SIZE = 10 * 1024 * 1024;   // 10MB
    const MAX_TOTAL_SIZE = 20 * 1024 * 1024;  // 20MB


    /*
     * 허용 이미지 MIME 타입
     */

    const ALLOWED_TYPES = [
        "image/jpeg",
        "image/png",
        "image/gif",
        "image/webp"
    ];


    /*
     * 선택된 파일
     */

    let selectedFiles = [];

    // 삭제하려는 파일들
    let deletedFileIds = new Set();
    /*
     * 제목 글자 수
     */

    title.addEventListener("input", () => {

        if (title.value.length > MAX_TITLE_LENGTH) {

            title.value =
                title.value.substring(0, MAX_TITLE_LENGTH);
        }

        const length = title.value.length;

        titleCount.textContent = length;

        if (length >= MAX_TITLE_LENGTH) {

            titleMessage.textContent =
                "제목은 10자까지 입력할 수 있습니다.";

        } else {

            titleMessage.textContent = "";
        }
    });

    // 그냥 스크롤할때 활성화된 요소가 textarea면 focus blur처리
addEventListener('scroll',()=>{
    if(document.activeElement==content)content.blur()
})
    /*
     * 본문 글자 수
     */

    content.addEventListener("input", () => {

        if (content.value.length > MAX_CONTENT_LENGTH) {

            content.value =
                content.value.substring(0, MAX_CONTENT_LENGTH);
        }

        const length = content.value.length;

        contentCount.textContent = length;

        if (length >= MAX_CONTENT_LENGTH) {

            contentMessage.textContent =
                "본문은 300자까지 입력할 수 있습니다.";

        } else {

            contentMessage.textContent = "";
        }
    });


    /*
     * 카테고리 변경
     */

    category.addEventListener("change", () => {

        if (category.value === "DATA") {

            fileUploadGroup.hidden = false;

        } else {

            fileUploadGroup.hidden = true;

            clearFiles();
        }
    });


    /*
     * 파일 선택 버튼
     */

    fileSelectButton.addEventListener("click", () => {

        fileInput.click();
    });


    /*
     * 파일 선택
     */

    fileInput.addEventListener("change", (event) => {

        addFiles(event.target.files);

        // 같은 파일을 다시 선택할 수 있도록 초기화
        fileInput.value = "";
    });


    /*
     * Drag Over
     */

    dropZone.addEventListener("dragover", (event) => {

        event.preventDefault();

        dropZone.classList.add("drag-over");
    });


    /*
     * Drag Leave
     */

    dropZone.addEventListener("dragleave", () => {

        dropZone.classList.remove("drag-over");
    });


    /*
     * Drop
     */

    dropZone.addEventListener("drop", (event) => {

        event.preventDefault();

        dropZone.classList.remove("drag-over");

        addFiles(event.dataTransfer.files);
    });


    /*
     * 파일 추가
     */

    function addFiles(files) {

        if (!files || files.length === 0) {
            return;
        }


        for (const file of files) {

            /*
             * 파일 형식 검사
             */

            if (!ALLOWED_TYPES.includes(file.type)) {

                showFileError(
                    `"${file.name}"은(는) 업로드할 수 없는 파일 형식입니다.\n` +
                    "JPG, JPEG, PNG, GIF, WEBP 이미지만 업로드할 수 있습니다."
                );

                continue;
            }


            /*
             * 개별 파일 용량 검사
             */

            if (file.size > MAX_FILE_SIZE) {

                showFileError(
                    `"${file.name}"의 용량이 10MB를 초과했습니다.`
                );

                continue;
            }


            /*
             * 빈 파일 검사
             */

            if (file.size === 0) {

                showFileError(
                    `"${file.name}"은(는) 빈 파일입니다.`
                );

                continue;
            }


            /*
             * 중복 파일 검사
             */

            const duplicate = selectedFiles.some(existingFile =>

                existingFile.name === file.name &&
                existingFile.size === file.size &&
                existingFile.lastModified === file.lastModified

            );


            if (duplicate) {

                showFileError(
                    `"${file.name}"은(는) 이미 선택되어 있습니다.`
                );

                continue;
            }


            /*
             * 전체 용량 검사
             */

            const currentTotal = getTotalSize();

            if (currentTotal + file.size > MAX_TOTAL_SIZE) {

                showFileError(
                    `"${file.name}"을(를) 추가하면 전체 용량이 20MB를 초과합니다.\n` +
                    "전체 파일 용량은 최대 20MB까지 가능합니다."
                );

                continue;
            }


            /*
             * 정상 파일 추가
             */

            selectedFiles.push(file);
        }


        renderFileList();
    }


    /*
     * 오류 메시지
     */

    function showFileError(message) {

        fileError.textContent = message;

        fileError.hidden = false;

        clearTimeout(showFileError.timer);

        showFileError.timer = setTimeout(() => {

            fileError.hidden = true;

        }, 5000);
    }


    /*
     * 파일 목록 출력
     */

    function renderFileList() {

        fileList.innerHTML = "";


        selectedFiles.forEach((file, index) => {

            const item =
                document.createElement("div");

            item.className = "file-item";


            /*
             * 미리보기
             */

            const preview =
                document.createElement("div");

            preview.className = "file-preview";


            if (file.type.startsWith("image/")) {

                const image =
                    document.createElement("img");

                image.alt = file.name;

                image.src =
                    URL.createObjectURL(file);

                preview.appendChild(image);
            }


            /*
             * 파일 정보
             */

            const info =
                document.createElement("div");

            info.className = "file-info";


            const name =
                document.createElement("div");

            name.className = "file-name";

            name.textContent = file.name;


            const size =
                document.createElement("div");

            size.className = "file-size";

            size.textContent =
                formatFileSize(file.size);


            info.appendChild(name);
            info.appendChild(size);


            /*
             * 삭제 버튼
             */

            const removeButton =
                document.createElement("button");

            removeButton.type = "button";

            removeButton.className = "file-remove";

            removeButton.textContent = "삭제";


            removeButton.addEventListener("click", () => {

                removeFile(index);
            });


            item.appendChild(preview);
            item.appendChild(info);
            item.appendChild(removeButton);

            fileList.appendChild(item);
        });


        updateFileInfo();
    }


    /*
     * 파일 삭제
     */

    function removeFile(index) {

        selectedFiles.splice(index, 1);

        renderFileList();
    }


    /*
     * 전체 파일 삭제
     */

    function clearFiles() {

        selectedFiles = [];

        fileList.innerHTML = "";

        fileError.hidden = true;

        updateFileInfo();
    }


    /*
     * 전체 파일 크기
     */

    function getTotalSize() {

        return getExistingFileTotalSize()
         + selectedFiles.reduce(
                (total, file) => total + file.size,
                0
           );
    }


    /*
     * 파일 정보 갱신
     */

    function updateFileInfo() {
    const existingCount = document.querySelectorAll(".existing-file-item:not([data-deleted='true'])").length;

    const totalCount = existingCount + selectedFiles.length;

    const total = getTotalSize();

    fileCount.textContent = totalCount;

    totalSize.textContent = formatFileSize(total);
        // const total = getTotalSize();

        // fileCount.textContent =
        //     selectedFiles.length;

        // totalSize.textContent =
        //     formatFileSize(total);
    }


    /*
     * 파일 크기 표시
     */

    function formatFileSize(bytes) {

        if (bytes === 0) {
            return "0 MB";
        }

        const mb =
            bytes / (1024 * 1024);

        return mb.toFixed(2) + " MB";
    }


    /*
     * 폼 제출
     */

    form.addEventListener("submit", (event) => {
        /*
         * 제목 검증
         */
        if (title.value.trim().length === 0) {

            event.preventDefault();

            alert("제목을 입력해주세요.");

            title.focus();

            return;
        }


        if (title.value.length > MAX_TITLE_LENGTH) {

            event.preventDefault();

            alert("제목은 최대 10자까지 입력할 수 있습니다.");

            title.focus();

            return;
        }


        /*
         * 본문 검증
         */

        if (content.value.trim().length === 0) {

            event.preventDefault();

            alert("내용을 입력해주세요.");

            content.focus();

            return;
        }


        if (content.value.length > MAX_CONTENT_LENGTH) {

            event.preventDefault();

            alert("본문은 최대 300자까지 입력할 수 있습니다.");

            content.focus();

            return;
        }


        /*
         * 자료실이 아닌 경우
         * 파일을 전부 제거
         */

        if (category.value !== "DATA") {

            clearFiles();

            return;
        }


        /*
         * 최종 파일 용량 검증
         */

        if (getTotalSize() > MAX_TOTAL_SIZE) {

            event.preventDefault();

            alert(
                "전체 파일 용량은 20MB를 초과할 수 없습니다."
            );

            return;
        }


        /*
         * 선택된 파일만 실제 input에 넣기
         */

        const dataTransfer =
            new DataTransfer();


        selectedFiles.forEach(file => {

            dataTransfer.items.add(file);

        });


        fileInput.files =
            dataTransfer.files;
    });


    /*
     * 초기 상태
     */

    if (category.value !== "DATA") {

        fileUploadGroup.hidden = true;

    } else {

        fileUploadGroup.hidden = false;
    }


    document.querySelectorAll(".existing-file-remove").forEach(button => {
        console.log("파일 삭제 버튼 적용");
        button.addEventListener("click", () => {

            const fileId = button.dataset.fileId;

            removeExistingFile(fileId);
        });
    });

        // 기존 파일 용량
    function getExistingFileTotalSize() {

        const existingFiles =
            document.querySelectorAll(
                ".existing-file-item"
            );

        let total = 0;

        existingFiles.forEach(file => {

            if (file.dataset.deleted !== "true") {

                total += Number(file.dataset.fileSize);
            }
        });

        return total;
    }
    // 기존 파일 삭제 버튼
    function removeExistingFile(fileId) {
        console.log(fileId);
        
        const fileItem = document.getElementById(
                `existing-file-${fileId}`
            );

        if (!fileItem) {
            return;
        }

        deletedFileIds.add(String(fileId));

        fileItem.dataset.deleted = "true";

        fileItem.style.display = "none";

        updateDeletedFileInputs();
        updateFileInfo();
    }
    // 삭제 인풋 업데이트
    function updateDeletedFileInputs() {

        const container = document.getElementById("deletedFileIds");

        if (!container) {
            return;
        }

        container.innerHTML = "";

        deletedFileIds.forEach(fileId => {
            const input = document.createElement("input");

            input.type = "hidden";
            input.name = "deletedFileIds";
            input.value = fileId;

            container.appendChild(input);
        });
    }

// 수정 페이지에서 바로 이벤트 디스페치
title.dispatchEvent(new Event("input"));
content.dispatchEvent(new Event("input"));
});

