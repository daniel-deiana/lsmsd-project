$(document).ready(function () {
        let obj = document.getElementById("title_anime")
        let desc = document.getElementById("desc_anime")
        let img = document.getElementById("pic_anime")
        $.ajax({
                    url : "/api/currentAnime",
                    method : "get",
                    success: function(data) {
                        result = JSON.parse(data)
                        console.log(result)
                        obj.textContent = result['title']
                        desc.textContent = result['synopsis']
                        img.setAttribute('src', result['img_url'])
                        let characters = result['figures']
                        for (fig in characters){
                            let html = '' +
                                '<div">\n' +
                                '            <div>\n' +
                                '              <img src='+ characters[fig].url +' class="img-fluid" alt="">\n' +
                                '              <div>\n' +
                                '                <h4>'+ characters[fig].name + '</h4>\n' +
                                '              </div>\n' +
                                '              <div>\n' +
                                '                <a href="characters.html" title="More Details"><i class="bx bx-link"></i></a>\n' +
                                '              </div>\n' +
                                '            </div>\n' +
                                '          </div>'
                            $('#figures').append(html)

                        }
                        let reviews = result['reviews']
                        for(rev in reviews){
                            let html = '<div class="swiper-slide">' +
                                '<div class="testimonial-item"><p id="rev1">' +
                                '<i class="bx bxs-quote-alt-left quote-icon-left"></i>' + reviews[rev].text + '<i class="bx bxs-quote-alt-right quote-icon-right"></i></p><img src="../img/unkown_c.jpg" class="testimonial-img" alt=""><h3>' + reviews[rev].profile +' - '+ reviews[rev].score+'</h3></div></div>'
                            $('#swiper').append(html)
                        }
                    }
        })
});