import com.muzi.utils.LoadRunnerUtils;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;


public class RechargeControllerTest {
    RestTemplate restTemplate = new RestTemplate();

    /**
     * 重置一下数据，会将订单状态置为0（处理中），账户余额置为0，方便压测结束后看效果，压测后看这2个值和期望的是不是一致
     *
     * @param rechargeId
     */
    public String accountAndRechargeInfo(String rechargeId) {
        return this.restTemplate.getForObject("http://localhost:8080/accountAndRechargeInfo?rechargeId=" + rechargeId, String.class);
    }

    /**
     * 重置一下数据，会将订单状态置为0（处理中），账户余额置为0，方便压测结束后看效果，压测后看这2个值和期望的是不是一致
     *
     * @param rechargeId
     */
    public void reset(String rechargeId) {
        this.restTemplate.getForObject("http://localhost:8080/reset?rechargeId=" + rechargeId, Boolean.class);
    }

    @Test
    public void rechargeCallBack1() throws InterruptedException {
        String rechargeId = "1";
        this.run("方案1", "rechargeCallBack1", rechargeId);
    }

    @Test
    public void rechargeCallBack2() throws InterruptedException {
        String rechargeId = "1";
        this.run("方案2", "rechargeCallBack2", rechargeId);
    }

    @Test
    public void rechargeCallBack3() throws InterruptedException {
        String rechargeId = "1";
        this.run("方案3", "rechargeCallBack3", rechargeId);
    }

    public void run(String name, String method, String rechargeId) throws InterruptedException {
        System.out.println(String.format("\n\n\n-----------------------------%s 幂等测试----------------------------------------", name));

        //先重置一下数据
        this.reset(rechargeId);

        //下面对回调接口模拟同时100次回调，看接口执行前后，订单的状态、账号余额是否和期望一致
        System.out.println(String.format("\n测试前，充值订单&账户信息：%s\n", this.accountAndRechargeInfo(rechargeId)));
        String url = String.format("http://localhost:8080/%s?rechargeId=%s", method, rechargeId);
        System.out.println(String.format("接口：%s", url));

        // 这里使用压测工具模拟同时发送100个请求
        LoadRunnerUtils.run(100, 100, () -> {
            try {
                this.restTemplate.postForObject(url, null, Boolean.class);
            } catch (Exception e) {
            }
        });
        System.out.println(String.format("\n测试后，充值订单&账户信息：%s\n\n\n", this.accountAndRechargeInfo(rechargeId)));
    }


}
