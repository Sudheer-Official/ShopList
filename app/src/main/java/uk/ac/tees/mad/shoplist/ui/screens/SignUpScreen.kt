package uk.ac.tees.mad.shoplist.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.shoplist.R
import uk.ac.tees.mad.shoplist.data.remote.FirebaseAuthResult
import uk.ac.tees.mad.shoplist.ui.viewmodels.SignUpViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onBackClick: () -> Unit,
    onSignUp: () -> Unit,
    signUpViewModel: SignUpViewModel = koinViewModel<SignUpViewModel>()
) {
    val email by signUpViewModel.email.collectAsStateWithLifecycle()
    val password by signUpViewModel.password.collectAsStateWithLifecycle()
    val isPasswordVisible by signUpViewModel.isPasswordVisible.collectAsStateWithLifecycle()
    val isSignUpMode by signUpViewModel.isSignUpMode.collectAsStateWithLifecycle()
    val signUpResult by signUpViewModel.signUpResult.collectAsStateWithLifecycle()

    val focusManager = LocalFocusManager.current
    val focusRequesterEmail = remember { FocusRequester() }
    val focusRequesterPassword = remember { FocusRequester() }

    Scaffold(
        topBar = {
        TopAppBar(
            title = {
            Text(
                "Sign Up", fontWeight = FontWeight.Bold
            )
        }, navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
        )
    },
        floatingActionButton = {
            Button(
                onClick = {
                    signUpViewModel.signUp(email, password)
                    signUpViewModel.switchSignUpMode()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                enabled = email.isNotBlank() && password.isNotBlank(),
                shape = MaterialTheme.shapes.extraLarge,
            ) {
                Icon(
                    imageVector = Icons.Default.HowToReg,
                    contentDescription = "Sign Up",
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sign Up",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        if (!isSignUpMode) {
            when (val result = signUpResult) {
                is FirebaseAuthResult.Loading -> {
                    AlertDialog(onDismissRequest = {}, icon = {
                        Icon(
                            Icons.Default.CloudUpload,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                    }, title = {
                        Text(
                            text = "Signing Up", fontWeight = FontWeight.Bold
                        )
                    }, text = {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(48.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                        }
                    }, confirmButton = { })
                }

                is FirebaseAuthResult.Success -> {
                    // Handle successful sign-up
                    AlertDialog(icon = {
                        Icon(
                            Icons.Default.CloudDone,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                    }, title = {
                        Text(
                            text = "Sign Up Successful", fontWeight = FontWeight.Bold
                        )
                    }, text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "You have successfully signed up."
                            )
                        }
                    }, confirmButton = {
                        TextButton(onClick = {
                            onSignUp()
                        }) {
                            Text(
                                text = "Go to Log In Screen", fontWeight = FontWeight.Bold
                            )
                        }
                    }, onDismissRequest = {
                        onSignUp()
                    })

                }

                is FirebaseAuthResult.Error -> {
                    // Handle sign-up error
                    AlertDialog(icon = {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }, title = {
                        Text(
                            text = "Error", fontWeight = FontWeight.Bold
                        )
                    }, text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(result.exception.message.toString())
                        }
                    }, confirmButton = {
                        TextButton(onClick = {
                            signUpViewModel.switchSignUpMode()
                        }) {
                            Text(text = "Retry?", fontWeight = FontWeight.Bold)
                        }
                    }, onDismissRequest = {
                        signUpViewModel.switchSignUpMode()
                    })
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(color = MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_shoplist_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .clip(MaterialTheme.shapes.extraLarge)
                    .size(80.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.extraLarge
                    ),
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Sign Up",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Email TextField
            OutlinedTextField(
                value = email,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .focusRequester(focusRequesterEmail),
                onValueChange = {
                    signUpViewModel.updateEmail(it)
                },
                label = {
                    Text(
                        text = "Email"
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Email,
                        contentDescription = "Email",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusRequesterPassword.requestFocus()
                }),
                shape = MaterialTheme.shapes.extraLarge,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password TextField
            OutlinedTextField(
                value = password,
                onValueChange = {
                    signUpViewModel.updatePassword(it)
                },
                label = {
                    Text(
                        text = "Password"
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Lock,
                        contentDescription = "Password",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .focusRequester(focusRequesterPassword),
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                }),
                shape = MaterialTheme.shapes.extraLarge,
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = {
                        signUpViewModel.togglePasswordVisibility()
                    }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = "Toggle Password Visibility",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                })

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account?", textAlign = TextAlign.Center
                )
                TextButton(
                    onClick = {
                        onBackClick()
                    }) {
                    Icon(
                        Icons.AutoMirrored.Filled.Login,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Log In",
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}