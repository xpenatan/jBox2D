// SPDX-FileCopyrightText: 2026 Xpe
// SPDX-License-Identifier: Apache-2.0

#include "box2d/base.h"

#include <emscripten/console.h>
#include <emscripten/emscripten.h>
#include <stddef.h>
#include <stdarg.h>

typedef int ( *b2WebCompareFcn )( const void*, const void* );

static void b2WebSwapBytes( unsigned char* a, unsigned char* b, size_t size )
{
    for ( size_t i = 0; i < size; ++i )
    {
        unsigned char value = a[i];
        a[i] = b[i];
        b[i] = value;
    }
}

static void b2WebSiftDown( unsigned char* data, size_t start, size_t count, size_t size, b2WebCompareFcn compare )
{
    if ( count < 2 )
    {
        return;
    }

    size_t root = start;
    while ( root <= ( count - 2 ) / 2 )
    {
        size_t child = 2 * root + 1;
        size_t largest = root;

        if ( compare( data + largest * size, data + child * size ) < 0 )
        {
            largest = child;
        }

        if ( child + 1 < count && compare( data + largest * size, data + ( child + 1 ) * size ) < 0 )
        {
            largest = child + 1;
        }

        if ( largest == root )
        {
            return;
        }

        b2WebSwapBytes( data + root * size, data + largest * size, size );
        root = largest;
    }
}

// TeaVM's Emscripten main module does not export qsort for side modules.
// Box2D uses qsort to order sensor overlaps, so keep a heap-sort
// implementation inside the Box2D side module.
void qsort( void* base, size_t count, size_t size, b2WebCompareFcn compare )
{
    if ( base == NULL || compare == NULL || count < 2 || size == 0 )
    {
        return;
    }

    unsigned char* data = base;

    for ( size_t start = count / 2; start > 0; --start )
    {
        b2WebSiftDown( data, start - 1, count, size, compare );
    }

    for ( size_t end = count; end > 1; )
    {
        --end;
        b2WebSwapBytes( data, data + end * size, size );
        b2WebSiftDown( data, 0, end, size, compare );
    }
}

static char* b2WebAppendText( char* cursor, const char* end, const char* text )
{
    while ( text != NULL && *text != '\0' && cursor < end )
    {
        *cursor++ = *text++;
    }
    return cursor;
}

static char* b2WebAppendPositiveInt( char* cursor, const char* end, int value )
{
    char digits[16];
    int count = 0;
    unsigned int number = (unsigned int)value;
    do
    {
        digits[count++] = (char)( '0' + number % 10 );
        number /= 10;
    }
    while ( number != 0 && count < (int)sizeof( digits ) );

    while ( count > 0 && cursor < end )
    {
        *cursor++ = digits[--count];
    }
    return cursor;
}

// Emscripten folds Box2D's integer-only assertion printf into iprintf, which
// is not exported by TeaVM's main module. Keep the assertion diagnostic in
// this side module so web failures retain the condition and source location.
int iprintf( const char* format, ... )
{
    va_list arguments;
    va_start( arguments, format );
    const char* condition = va_arg( arguments, const char* );
    const char* fileName = va_arg( arguments, const char* );
    int lineNumber = va_arg( arguments, int );
    va_end( arguments );

    (void)format;
    char message[1024];
    char* cursor = message;
    const char* end = message + sizeof( message ) - 1;
    cursor = b2WebAppendText( cursor, end, "BOX2D ASSERTION: " );
    cursor = b2WebAppendText( cursor, end, condition );
    cursor = b2WebAppendText( cursor, end, ", " );
    cursor = b2WebAppendText( cursor, end, fileName );
    cursor = b2WebAppendText( cursor, end, ", line " );
    cursor = b2WebAppendPositiveInt( cursor, end, lineNumber );
    *cursor = '\0';
    emscripten_console_error( message );

    return (int)( cursor - message );
}

// TeaVM's Emscripten main module does not export remainderf for side modules.
// Keep Box2D's b2UnwindAngle semantics local to the Box2D module, including
// round-to-nearest with ties to an even quotient.
float remainderf( float x, float y )
{
    float quotient = x / y;
    int32_t nearest = (int32_t)quotient;
    float fraction = quotient - (float)nearest;
    if ( fraction > 0.5f || ( fraction == 0.5f && ( nearest & 1 ) != 0 ) )
    {
        nearest += 1;
    }
    else if ( fraction < -0.5f || ( fraction == -0.5f && ( nearest & 1 ) != 0 ) )
    {
        nearest -= 1;
    }
    return x - (float)nearest * y;
}

uint64_t b2GetTicks( void )
{
    return (uint64_t)( emscripten_get_now() * 1000000.0 );
}

float b2GetMilliseconds( uint64_t ticks )
{
    uint64_t ticksNow = b2GetTicks();
    return (float)( ( ticksNow - ticks ) / 1000000.0 );
}

float b2GetMillisecondsAndReset( uint64_t* ticks )
{
    uint64_t ticksNow = b2GetTicks();
    float milliseconds = (float)( ( ticksNow - *ticks ) / 1000000.0 );
    *ticks = ticksNow;
    return milliseconds;
}

void b2Yield( void )
{
}

uint32_t b2Hash( uint32_t hash, const uint8_t* data, int count )
{
    uint32_t result = hash;
    for( int i = 0; i < count; ++i )
    {
        result = ( result << 5 ) + result + data[i];
    }
    return result;
}
