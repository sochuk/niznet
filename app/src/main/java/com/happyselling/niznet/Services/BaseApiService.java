package com.happyselling.niznet.Services;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

import com.google.gson.JsonObject;
import com.happyselling.niznet.Models.BrandsResponse.Brands;
import com.happyselling.niznet.Models.CartResponse.CartList;
import com.happyselling.niznet.Models.CategoryResponse.Category;
import com.happyselling.niznet.Models.DetailTransaksiResponse.DetailTrx;
import com.happyselling.niznet.Models.LoginResponse.UserLoginResponse;
import com.happyselling.niznet.Models.MasterBankResponse.MasterBank;
import com.happyselling.niznet.Models.MasterPaymentMethod.PaymentMethod;
import com.happyselling.niznet.Models.MasterTokoResponse.MasterToko;
import com.happyselling.niznet.Models.MasterTransaksiResponse.MasterTransaksi;
import com.happyselling.niznet.Models.Meta;
import com.happyselling.niznet.Models.NewProductResponse.Product;
import com.happyselling.niznet.Models.ProductResponse.ProductList;

import java.util.ArrayList;

public interface BaseApiService {
    /*USER*/
    @POST("login")
    Call<UserLoginResponse> postLogin(@Body JsonObject loginParam);

    @Multipart
    @POST("update-profile")
    Call<UserLoginResponse> updateProfile(@Part("name") RequestBody name,
                                          @Part("email") RequestBody email,
                                          @Part("password") RequestBody password,
                                          @Part("telp") RequestBody telpon,
                                          @Part("address") RequestBody adress,
                                          @Part MultipartBody.Part photo,
                                          @Part MultipartBody.Part ktp
                                          );

    @POST("cek-email")
    Call<Meta> checkEmailReg(@Body JsonObject email);

    @Multipart
    @POST("register")
    Call<Meta> postRegister(@Part("name") RequestBody name,
                            @Part("email") RequestBody email,
                            @Part("id_level") RequestBody idLevel,
                            @Part("password") RequestBody password,
                            @Part("password_confirmation") RequestBody passwordConfirm,
                            @Part("telp") RequestBody telpon,
                            @Part("address") RequestBody adress);
    /*USER*/

    /*PRODUCT*/
    @GET("produk/get-list-brand")
    Call<Brands> getAllBrands();

    @GET("produk/get-list-kategori")
    Call<Category> getAllCategory();

    @GET("produk/get-list-new-product")
    Call<Product> getNewProduct();

    @GET("produk/get-list")
    Call<ProductList> getProductList(@Query("page") long page,
                                     @Query("limit") int limit,
                                     @Query("where_value") String whereValue);
    /*PRODUCT*/

    /*TRANSACTION*/
    @POST("transaction/add-cart")
    Call<Meta> postCart(@Body JsonObject postCartParams);

    @GET("transaction/get-list-cart")
    Call<CartList> getCartList();

    @POST("transaction/delete")
    Call<Meta> deleteCartById(@Body JsonObject idCart);

    @Multipart
    @POST("transaction/checkout")
    Call<Meta> postCheckout(@Part("id[]") ArrayList<Integer> idCart, @Part("quantity[]") ArrayList<Integer> quantity,
                            @Part("totalPrice") RequestBody totalPrice, @Part("id_toko") Integer idToko,
                            @Part("payment_method") Integer paymentMethod,
                            @Part("nama_bank") String namaBank);

    @GET("transaction/get-list-master-trx")
    Call<MasterTransaksi> getMasterTransaksi(@Query("page") long page,
                                             @Query("limit") int limit,
                                             @Query("status") String status);


    @GET("get-toko-list")
    Call<MasterToko> getMasterToko();
    @GET("get-payment-method")
    Call<PaymentMethod> getPaymentMethod();

    @GET("get-bank-list")
    Call<MasterBank> getMasterBank();
    /*TRANSACTION*/
    @GET("tracking/get-detail-list")
    Call<DetailTrx> getDetailTransaksi(@Query("id") Integer idTrx);

    @Multipart
    @POST("tracking/upload-img")
    Call<Meta> postBuktiPembayaran(@Part("id") RequestBody idTrx,
                                   @Part MultipartBody.Part photo);

    @POST("tracking/update-status")
    Call<Meta> postSubmitPesanan(@Body JsonObject jsonObject);


}
