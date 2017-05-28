package cn.advu.workflow.web.common.interceptor;


import cn.advu.workflow.common.constant.Constants;
import cn.advu.workflow.web.common.RequestUtil;
import cn.advu.workflow.web.common.exception.LoginException;
import cn.advu.workflow.web.common.loginContext.LoginTools;
import cn.advu.workflow.web.common.loginContext.LoginUser;
import cn.advu.workflow.web.common.loginContext.UserThreadLocalContext;
import cn.advu.workflow.web.user.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 根据cookie 判断是否登录
 * 
 *
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {

	private final static Log logger = LogFactory.getLog(LoginInterceptor.class);

	@Autowired
	private UserService adminUserService;

	/**
	 *
	 * @param request
	 * @param response
	 * @param handler
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@Override
	public final boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {

		//读取cookie
		//获取用户数据
		//用户数据存进loginContext

		String loginCookie = RequestUtil.getCookieValue(request, Constants.Login.LOGIN_COOKIE_KEY);

		if (StringUtils.isBlank(loginCookie)) {

			response.sendRedirect(Constants.Login.LOGIN_URL);

			return false;

		}
		try {

			LoginUser loginUser = LoginTools.parseLoginUser(loginCookie);

			adminUserService.validLoginUser(loginUser);

			UserThreadLocalContext.addCurrentUser(loginUser);

			request.setAttribute(Constants.Login.LOGIN_USER_ATTR_KEY,loginUser);

			if (logger.isDebugEnabled()) {
				logger.debug(loginUser);
			}

			return true;


		} catch (LoginException e) {
			logger.error( e.getMessage());

			response.sendRedirect(Constants.Login.LOGIN_URL);

			return false;
		}

	}


	/**
	 * 删除用户状态
	 */
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserThreadLocalContext.remove();
	}

}
