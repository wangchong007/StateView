# StateView
- A library for management state.

### Download : 

```
implementation 'com.peaut.stateview:StateView:0.1.0'
```

### How do I use StateView?

```
override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setStateLayout()
        show_content.setOnClickListener {
            stateView?.showContent()
        }
        show_empty.setOnClickListener {
            stateView?.showEmpty()
        }
        show_retry.setOnClickListener {
            stateView?.showRetry()
        }
        show_loading.setOnClickListener {
            stateView?.showLoading()
        }
    }

    private fun setStateLayout() {
        //content_id is the rootView id when you want to manage the state
        stateView = StateView.inject(findViewById(R.id.content_id))
        //retry listener 
        stateView?.setOnRetryClickListener {
            Toast.makeText(this,"click retry",Toast.LENGTH_LONG).show()
        }
    }
```

- You can customize the layout

```
stateView?.setEmptyResource(R.layout.empty_layout) //setEmptyResource() must before showEmpty()
stateView?.setRetryResource(R.layout.retry_layout) //setRetryResource() must before showRetry()
stateView?.setLoadingResource(R.layout.loading_layout) //setLoadingResource() must before showLoading()

//add retry listener 
stateView?.setOnRetryClickListener{
    // do something
}
```
