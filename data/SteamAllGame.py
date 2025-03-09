import requests

# 게임 상세정보 가져오기
def getGameDetails(appid):
    url = f"https://store.steampowered.com/api/appdetails?appids={appid}&l=korean"
    headers = {"Content-Type": "application/json"} 
    timeout = 60  # 60초 타임아웃
    session = requests.Session()
    session.headers.update(headers)

    try:
        response = session.get(url, timeout=timeout)
        response.raise_for_status()  # HTTP 오류 발생 시 예외 발생

        result = response.json().get(str(appid), {})
        if result.get("success"):
            return result.get("data")  # success가 True일 때만 data 반환
        else:
            return None  # success가 False이면 None 반환
    except requests.exceptions.RequestException as e:
        print(f"Error: {e}")
        return None

# 전체 게임 리스트 가져오기
def getGameList():
    response = requests.get("https://api.steampowered.com/ISteamApps/GetAppList/v2")

    if response.status_code == 200:
        # json 추출
        allGameList = response.json()["applist"]["apps"]

        for game in allGameList:
            appid = game["appid"]
            gameDetail = getGameDetails(appid)
            # GAMMOA_GAME_MAS
            # name = 게임명
            # publishers = 배급사 ? 개발사 ?
            # header_image 메인 이미지?
            # screenshots.path_thumbnail = 썸네일
            # detailed_description = 게임 설명

            # GAMMOA_GP_MAS
            # initial = 정가
            # release_date.date = 출시일
            # genres 장르
            
            # GAMMOA_DISGAME_MAS
            # final = 최종가격?
            # discount_percent = 할인률
            # 할인 시작일
            # 할인 종료일 
            
            # inform
            # - 평점 컬럼 삭제 : 플랫폼 별 평점 시스템이 다름
            # - 트레일러 URL 컬럼 삭제 : 스팀 api에 트레일러 영상 주소 없음
            # - 회사 로고 컬럼 삭제 : 개발자 로고 사진 없음
            # - 할인 시작일 종료일 찾아야됨
            
            print(gameDetail)
    else:
        print("Error")

getGameList()
