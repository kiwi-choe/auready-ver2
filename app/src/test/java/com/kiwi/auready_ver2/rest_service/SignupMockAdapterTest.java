package com.kiwi.auready_ver2.rest_service;

import com.kiwi.auready_ver2.data.api_model.ErrorResponse;
import com.kiwi.auready_ver2.data.api_model.SignupInfo;
import com.kiwi.auready_ver2.data.api_model.SignupResponse;
import com.kiwi.auready_ver2.login.IBaseUrl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.annotation.Annotation;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;
import retrofit2.Converter;

/**
 * Created by kiwi on 6/17/16.
 */
public class SignupMockAdapterTest {

    public static final String STUB_EMAIL = "dd@gmail.com";
    public static final String STUB_NAME = "nameOfdd";

    private MockRetrofit mockRetrofit;
    private Retrofit retrofit;

    @Before
    public void setUp() throws Exception {
        retrofit = new Retrofit.Builder().baseUrl(IBaseUrl.BASE_URL)
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NetworkBehavior behavior = NetworkBehavior.create();

        mockRetrofit = new MockRetrofit.Builder(retrofit)
                .networkBehavior(behavior)
                .build();
    }

    //Q mockService로 테스트를 하면, 실제 서버에 요청하여 받는 부분은 tdd로 어떻게 구현하지?
    @Test
    public void signupResponse() throws Exception {
        BehaviorDelegate<ISignupService> delegate = mockRetrofit.create(ISignupService.class);
        ISignupService mockSignupService = new MockSignupService(delegate);

        // Create the signupInfo stub
        String email = STUB_EMAIL;
        String password = "123";
        SignupInfo signupInfo = new SignupInfo(email, password);

        // Actual Test
        Call<SignupResponse> signupCall = mockSignupService.signupLocal(signupInfo);
        Response<SignupResponse> signupResponse = signupCall.execute();

        // Asserting response
        Assert.assertTrue(signupResponse.isSuccessful());
        Assert.assertEquals(email, signupResponse.body().getEmail());
    }

    @Test
    public void signupFailure() throws Exception {
        BehaviorDelegate<ISignupService> delegate = mockRetrofit.create(ISignupService.class);
        MockFailedSignupService mockSignupService = new MockFailedSignupService(delegate);

        // Create the signupInfo stub
        String email = "bbb@bbb.bbb";
        String password = "123";
        SignupInfo signupInfo = new SignupInfo(email, password);

        Call<SignupResponse> signupCall = mockSignupService.signupLocal(signupInfo);
        Response<SignupResponse> signupResponse = signupCall.execute();
        Assert.assertFalse(signupResponse.isSuccessful());

        Converter<ResponseBody, ErrorResponse> errorConverter = retrofit.responseBodyConverter(ErrorResponse.class, new Annotation[0]);
        ErrorResponse error = errorConverter.convert(signupResponse.errorBody());

        // Asserting response
        Assert.assertEquals(404, signupResponse.code());
        Assert.assertEquals(email + " is already registered", error.getMessage());
    }
}