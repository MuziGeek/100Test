package com.muzi.part8.aspect;


import com.muzi.part8.excel.ExcelExportResponse;
import com.muzi.part8.excel.ExcelExportUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


@Component
@Aspect
public class ExcelExportAspect {
    @Around(value = "execution(* com.muzi.*Controller.*(..))")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        if (result instanceof ExcelExportResponse) {
            //下载excel
            ExcelExportUtils.writeExcelToResponse((ExcelExportResponse) result);
            return null;
        } else {
            return result;
        }
    }
}
