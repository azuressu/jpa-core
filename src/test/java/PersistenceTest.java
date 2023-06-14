import com.sparta.entity.Memo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PersistenceTest {

    EntityManagerFactory emf;
    EntityManager em;

    @BeforeEach
        // test 메서드가 5개가 있고, 5개를 실행한다면, entityfactory와 manager를 각각 만들기 때문
    void setUp() {
        // 여기서 주는 NAME이 persistence.xml 파일 내부의 persistence-unit 값임
        emf = Persistence.createEntityManagerFactory("memo");
        // 매니저 생성
        em = emf.createEntityManager();
    }

    @Test
    @DisplayName("1차 캐시 : Entity 저장")
    void test1() {
        EntityTransaction et = em.getTransaction();

        et.begin();

        try {

            Memo memo = new Memo();
            memo.setId(1L);
            memo.setUsername("Robbie");
            memo.setContents("1차 캐시 Entity 저장");

            em.persist(memo);

            et.commit();

        } catch (Exception ex) {
            ex.printStackTrace();
            et.rollback();
        } finally {
            em.close();
        }

        emf.close();
    } // test1() - 데이터 저장


    @Test
    @DisplayName("Entity 조회 : 캐시 저장소에 해당하는 Id가 존재하지 않은 경우")
    void test2() {
        try {

            Memo memo = em.find(Memo.class, 1); // 첫 번째에는 내가 찾고자 하는 엔티티 클래스 타입, 두 번째에는 정확한 PK 값을 주어야 함
            System.out.println("memo.getId() = " + memo.getId());
            System.out.println("memo.getUsername() = " + memo.getUsername());
            System.out.println("memo.getContents() = " + memo.getContents());


        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    } // test2() - 데이터 조회

    @Test
    @DisplayName("Entity 조회 : 캐시 저장소에 해당하는 Id가 존재하는 경우")
    void test3() {
        try {

            Memo memo1 = em.find(Memo.class, 1);         // 셀렉트 1
            System.out.println("memo1 조회 후 캐시 저장소에 저장\n");

            Memo memo2 = em.find(Memo.class, 1);         // 여기서는 더 이상 셀렉트 문을 날리지 않음
            System.out.println("memo2.getId() = " + memo2.getId());
            System.out.println("memo2.getUsername() = " + memo2.getUsername());
            System.out.println("memo2.getContents() = " + memo2.getContents());


        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    } // test3() - 데이터 조회

    @Test
    @DisplayName("객체 동일성 보장")
    void test4() {
        EntityTransaction et = em.getTransaction();

        et.begin();

        try {
            Memo memo3 = new Memo();
            memo3.setId(2L);
            memo3.setUsername("Robbert");
            memo3.setContents("객체 동일성 보장");
            em.persist(memo3);   // 저장하기

            Memo memo1 = em.find(Memo.class, 1);
            Memo memo2 = em.find(Memo.class, 1);
            Memo memo  = em.find(Memo.class, 2);

            System.out.println(memo1 == memo2);
            System.out.println(memo1 == memo);

            et.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            et.rollback();
        } finally {
            em.close();
        }

        emf.close();
    } // test4() - 객체 동일성 보장

    @Test
    @DisplayName("Entity 삭제")
    void test5() {
        EntityTransaction et = em.getTransaction();

        et.begin();

        try {

            Memo memo = em.find(Memo.class, 2);

            em.remove(memo);

            et.commit();

        } catch (Exception ex) {
            ex.printStackTrace();
            et.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }//test5() - 엔티티 삭제

    @Test
    @DisplayName("쓰기 지연 저장소 확인")
    void test6() {
        EntityTransaction et = em.getTransaction();

        et.begin();

        try {
            Memo memo = new Memo();
            memo.setId(2L);
            memo.setUsername("Robbert");
            memo.setContents("쓰기 지연 저장소");
            em.persist(memo);

            Memo memo2 = new Memo();
            memo2.setId(3L);
            memo2.setUsername("Bob");
            memo2.setContents("과연 저장을 잘 하고 있을까?");
            em.persist(memo2);

            System.out.println("트랜잭션 commit 전");
            et.commit();     // em.flush
            System.out.println("트랜잭션 commit 후");

        } catch (Exception ex) {
            ex.printStackTrace();
            et.rollback();
        } finally {
            em.close();
        }

        emf.close();
    } // test6() - 쓰기 지연 저장소

    @Test
    @DisplayName("flush() 메서드 확인")
    void test7() {
        EntityTransaction et = em.getTransaction();

        et.begin();

        try {
            Memo memo = new Memo();
            memo.setId(4L);
            memo.setUsername("Flush");
            memo.setContents("Flush() 메서드 호출");
            em.persist(memo);

            System.out.println("flush() 전");
            em.flush(); // flush() 직접 호출
            System.out.println("flush() 후\n");


            System.out.println("트랜잭션 commit 전");
            et.commit();
            System.out.println("트랜잭션 commit 후");

        } catch (Exception ex) {
            ex.printStackTrace();
            et.rollback();
        } finally {
            em.close();
        }

        emf.close();

        // 데이터 insert, update, delete 등 데이터 변경 sql을
        // 데이터베이스에 요청 및 반영하기 위해서는 꼭 트랜잭션 환경이 필요하다 !
    } // test7() - flush

    @Test
    @DisplayName("변경 감지 확인")
    void test8() {
        EntityTransaction et = em.getTransaction();

        et.begin();

        try {
            System.out.println("변경할 데이터를 조회합니다.");
            Memo memo = em.find(Memo.class, 4);
            System.out.println("memo.getId() = " + memo.getId());
            System.out.println("memo.getUsername() = " + memo.getUsername());
            System.out.println("memo.getContents() = " + memo.getContents());

            System.out.println("\n수정을 진행합니다.");
            memo.setUsername("Update");
            memo.setContents("변경 감지 확인");

            System.out.println("트랜잭션 commit 전");
            et.commit();  // flush 자동 호출
            System.out.println("트랜잭션 commit 후");

        } catch (Exception ex) {
            ex.printStackTrace();
            et.rollback();
        } finally {
            em.close();
        }

        emf.close();
    } // test8() - 변경 감지 확인
}
