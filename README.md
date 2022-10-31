# Spring Fw + Quartz + Batch   
Spring Framework 5.x    
Spring Quartz 2.3   
Spring Batch   
Maven   
***
1. Spring Batch + Spring Quartz   
스프링 배치 + 스프링 쿼츠   
2. Quartz clustering   
쿼츠 클러스터링   
3. add column at batch job meta table (job_execution) to save which scheduler(clustered server) launched batch job   
배치잡이 어느 스케쥴러(클러스터링된 서버)에서 실행되었는지 저장하기 위해, 배치 잡 메타테이블(job_execution)에 칼럼을 추가   
4. scheduler gracefully shutdown (need to check whether it works properly even if WAS shutdown..)   
스케쥴러 gracefully shutdown 적용(WAS 가 shutdown 될 때도 graceful 하게 shutdown 되는지 확인이 필요..)   
5. batch job TDD using junit   
junit 을 사용한 배치잡 테스트코드 작성   
***

6. batch, qrtz DB 정보
https://developyo.tistory.com/252

7.ui 는 quartz 관리 하지 않고 batch_job , batch_schedule 관리한다.
### TODO   
1. prevent duplicate schedule running  
스케쥴 중복 러닝 방지 

2. spring boot 로 변경 예정 https://oingdaddy.tistory.com/21
3. 화면
http://localhost:8080/batch/schedules.do 
