// 추천 프로그램 새로고침 이벤트
$("#viewMoreRecommendBtn").on("click", function() {

    var curPage = $("#recommendPage").val();
    var sendData = { page : Number(curPage) + 1}

    $.ajax({
        type: "get",
        url: "/program/add/recommend/list",
        data: sendData,
        dataType: "json",
        success: function(data) {
            if(data.result === "success") {
                var insertHtml = "";
                for(var i = 0; i < data.programRecommendList.length; i++) {
                    insertHtml += '<div class="col-xl-4 col-md-6">\n<div class="card bg-white text-dark mb-4">\n<div class="card-header">\n<div class="d-flex">\n<div class="col-md-10">\n<i class="fas fa-clone me-1"></i>'
                    insertHtml += '<span >프로그램' + (i+1) + '</span>\n'
                    insertHtml += '</div>\n<div class="col-md-2">\n'
                    insertHtml += '<a class="small text-dark stretched-link" href="/program/info/' + data.programRecommendList[i].id + '">자세히 보기</a>'
                    insertHtml += '</div>\n</div>\n</div>\n<div class="card-body">\n<table class="w-100">\n<tbody>\n<tr>\n'
                    insertHtml += '<td width="20%">제목</td>\n'
                    insertHtml += '<td width="60%">' + data.programRecommendList[i].title + '</td>\n'
                    insertHtml += '<td width="20%">' + data.programRecommendList[i].category + '</td>\n'
                    insertHtml += '</tr>\n<tr>\n<td style="font:bold;" width="20%" colspan="1">멘토</td>\n<td width="80%" colspan="2">\n'

                    if(data.programRecommendList[i].mentor != null) {
                        for(var j = 0; j < data.programRecommendList[i].mentor.length; j++) {
                            if(j != data.programRecommendList[i].mentor.length - 1)
                                insertHtml += '<span>' + data.programRecommendList[i].mentor[j].nickname + ', </span>\n';
                            else
                                insertHtml += '<span>' + data.programRecommendList[i].mentor[j].nickname + '</span>\n';
                        }
                    }

                    insertHtml += '</div>\n</td>\n</tr>\n<tr>\n'
                    insertHtml += '<td width="20%">모집기간</td>\n'
                    insertHtml += '<td width="80%">' + data.programRecommendList[i].dueDate + '까지</td>\n'
                    insertHtml += '</tr>\n<tr>\n'
                    insertHtml += '<td style="font:bold;" width="20%" colspan="1">목표</td>\n'
                    insertHtml += '<td width="80%" colspan="2">' + data.programRecommendList[i].goal + '</td>\n'
                    insertHtml += '</tr>\n</tbody>\n</table>\n</div>'
                    insertHtml += '<div class="card-footer d-flex align-items-center justify-content-between">\n'
                    insertHtml += '<a class="small text-dark stretched-link" href="/program/info/' + data.programRecommendList[i].id + '">자세히 보기</a>\n'
                    insertHtml += '<div class="small text-dark"><i class="fas fa-angle-right"></i></div>\n</div>\n</div>\n</div>'
                }
                var nextPage = Number($("#recommendPage").val()) + 1;
                $("#recommendPage").val(nextPage);
                $("#recommendArea").html(insertHtml);

                if(!data.hasNext)
                    $("#viewMoreRecommendBtn").hide();
            }
        },error:function(e){
                alert(e.code);
                alert(e.message);
         }
    });

})

// 모든 프로그램 더보기 버튼 이벤트
$('#viewMoreBtn').on("click", function() {

    var curPage = $("#allProgramPage").val();
    var sendData = { page : Number(curPage) + 1}

    $.ajax({
        type: "get",
        url: "/program/add/list",
        data: sendData,
        dataType: "json",
        success: function(data) {
            if(data.result === "success") {
                var insertHtml = "";
                for(var i=0; i < data.moreProgram.length; i++) {
                    insertHtml += '<div class="col-xl-3 programCard">\n';
                    insertHtml += '<div class="card mb-4">\n';
                    insertHtml += '<div class="card-header">\n';
                    insertHtml += '<div class="d-flex">\n';
                    insertHtml += '<div class="col-md-10">\n';
                    insertHtml += '<i class="fas fa-clone me-1"></i>\n';
                    insertHtml += '<span>프로그램' + ((curPage * 8) + i + 1) + '</span>\n';
                    insertHtml += '</div>\n<div class="col-md-2">\n';
                    insertHtml += '<a class="small text-dark stretched-link" href=/program/info/' + data.moreProgram[i].id + '">자세히 보기</a>\n';
                    insertHtml += '</div>\n</div>\n</div>\n<div class="card-body">\n<table class="w-100">\n<tbody>\n<tr>\n<td style="font:bold;" width="20%">제목</td>\n';
                    insertHtml += '<td width="80%">' + data.moreProgram[i].title + '</td>\n';
                    insertHtml += '</tr>\n<tr>\n<td style="font:bold;" width="20%">멘토</td>\n<td width="80%">';
                    if(data.moreProgram[i].mentor != null) {
                        for(var j = 0; j < data.moreProgram[i].mentor.length; j++) {
                            if(j != data.moreProgram[i].mentor.length - 1)
                                insertHtml += '<span>' + data.moreProgram[i].mentor[j].nickname + ', </span>\n';
                            else
                                insertHtml += '<span>' + data.moreProgram[i].mentor[j].nickname + '</span>\n';
                        }
                    }
                    insertHtml += '</div>\n</td>\n</tr>\n<tr>\n<td style="font:bold;" width="20%">인원</td>';
                    insertHtml += '<td width="80%">' + data.moreProgram[i].maxMember + '</td>\n';
                    insertHtml += '</tr>\n<tr>\n<td style="font:bold;" colspan="2" width="100%">프로그램 분류</td>\n</tr>\n<tr>';
                    insertHtml += '<td colspan="2" width="100%">' + data.moreProgram[i].category + '</td>\n';
                    insertHtml += '</tr>\n<tr>\n<td width="20%">모집기간</td>';
                    insertHtml += '<td width="80%">'+ data.moreProgram[i].dueDate + '까지 </td>\n';
                    insertHtml += '</tr>\n<tr>\n<td style="font:bold;" width="20%">목표</td>';
                    insertHtml += '<td width="80%">' + data.moreProgram[i].goal + '</td>\n';
                    insertHtml += '</tr>\n</tbody>\n</table>\n</div>\n</div>\n</div>';
                }

                var nextPage = Number($("#allProgramPage").val()) + 1;
                $("#allProgramPage").val(nextPage);
                $("#moreProgramSpace").before(insertHtml);

                if(!data.hasNext)
                    $('#viewMoreBtn').hide()
            }
        },error:function(e){
               alert(e.code);
               alert(e.message);
        }
    });
})